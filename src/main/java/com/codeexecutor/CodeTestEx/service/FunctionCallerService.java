package com.codeexecutor.CodeTestEx.service;

import com.codeexecutor.CodeTestEx.model.FunctionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class FunctionCallerService {
    private static final Logger logger = LoggerFactory.getLogger(FunctionCallerService.class);
    private static final int PROCESS_TIMEOUT_SECONDS = 30;
    private final ObjectMapper objectMapper;

    public FunctionCallerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Object executeFunction(FunctionRequest request) throws Exception {
        validateRequest(request);

        if ("python".equalsIgnoreCase(request.getType())) {
            return executePythonFunction(request);
        } else if ("javascript".equalsIgnoreCase(request.getType())) {
            return executeJavaScriptFunction(request);
        } else {
            throw new IllegalArgumentException("Tipo de lenguaje no soportado: " + request.getType());
        }
    }

    private void validateRequest(FunctionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }
        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de lenguaje es requerido");
        }
        if (request.getArchivo() == null || request.getArchivo().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo es requerido");
        }
        if (request.getNombrefuncion() == null || request.getNombrefuncion().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la función es requerido");
        }
    }

    private Path getFullPath(FunctionRequest request) {
        try {
            Path path = request.getRuta() != null && !request.getRuta().trim().isEmpty()
                    ? Paths.get(request.getRuta(), request.getArchivo())
                    : Paths.get(request.getArchivo());

            if (!Files.exists(path)) {
                throw new RuntimeException("El archivo no existe: " + path);
            }

            return path;
        } catch (SecurityException e) {
            throw new RuntimeException("Error de acceso al archivo: " + e.getMessage(), e);
        }
    }

    private Object executePythonFunction(FunctionRequest request) throws Exception {
        Path fullPath = getFullPath(request);
        List<String> command = buildPythonCommand(request, fullPath);

        return executeProcess(command, fullPath);
    }

    private List<String> buildPythonCommand(FunctionRequest request, Path fullPath) throws Exception {
        List<String> command = new ArrayList<>();
        command.add("python");
        command.add(fullPath.toString());
        command.add(request.getNombrefuncion());

        // Serializar los datos correctamente
        if (request.getData() != null) {
            String jsonData = objectMapper.writeValueAsString(request.getData())
                    .replace("\"", "\\\""); // Escapar las comillas dobles
            command.add(jsonData);
        }

        // Agregar la variable de tienda si existe
        if (request.getVariable() != null && !request.getVariable().isEmpty()) {
            command.add(request.getVariable().get(0).toString());
        }

        return command;
    }
    private Object executeProcess(List<String> command, Path workingDir) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(workingDir.getParent().toFile());
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        if (!process.waitFor(PROCESS_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            throw new RuntimeException("El proceso excedió el tiempo límite de " + PROCESS_TIMEOUT_SECONDS + " segundos");
        }

        if (process.exitValue() != 0) {
            throw new RuntimeException("Error en la ejecución: " + output);
        }

        return output.toString().trim();
    }

    private Object executeJavaScriptFunction(FunctionRequest request) throws Exception {
        Path fullPath = getFullPath(request);
        logger.info("Ejecutando JavaScript - Ruta: {}", fullPath);

        try (Context context = Context.newBuilder("js")
                .allowAllAccess(true)
                .build()) {

            String scriptContent = Files.readString(fullPath);
            context.eval("js", scriptContent);

            Value jsBindings = context.getBindings("js");
            Value function = jsBindings.getMember(request.getNombrefuncion());

            if (!function.canExecute()) {
                throw new RuntimeException("La función '" + request.getNombrefuncion() + "' no existe o no es ejecutable");
            }

            Object[] args = request.getVariable() != null
                    ? request.getVariable().toArray()
                    : new Object[0];

            Value result = function.execute(args);
            return result.isNull() ? null : result.toString();

        } catch (Exception e) {
            logger.error("Error ejecutando JavaScript", e);
            throw new RuntimeException("Error ejecutando JavaScript: " + e.getMessage(), e);
        }
    }
}