package br.com.muita_conta.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handler para exceções de violação de integridade do banco de dados (ex: chaves duplicadas).
     * Retorna um status HTTP 409 (Conflict).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        String message = "Erro de integridade de dados. O recurso pode já existir ou estar violando uma restrição.";
        // Loga a causa raiz do erro, que é mais informativa
        log.error("Exceção de integridade de dados na rota {}: {}", request.getRequestURI(), ex.getMostSpecificCause().getMessage(), ex);
        ApiExceptionResponse response = createErrorResponse(HttpStatus.CONFLICT, message, request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handler específico para erros de argumento inválido, como um token inválido.
     * Retorna 401 Unauthorized para o cliente com a mensagem específica do erro.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Argumento inválido na rota {}: {}", request.getRequestURI(), ex.getMessage());
        ApiExceptionResponse response = createErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handler para falhas internas de autenticação do Spring Security.
     * Retorna um status HTTP 401 (Unauthorized).
     */
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<Object> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException ex, HttpServletRequest request) {
        log.error("Erro de autenticação na rota {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ApiExceptionResponse response = createErrorResponse(HttpStatus.UNAUTHORIZED, "Erro de autenticação: Credenciais inválidas.", request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handler para tentativas de acesso a recursos proibidos (falha de autorização).
     * Retorna um status HTTP 403 (Forbidden).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Acesso negado na rota {}: {}", request.getRequestURI(), ex.getMessage());
        ApiExceptionResponse response = createErrorResponse(HttpStatus.FORBIDDEN, "Acesso negado. Você não tem permissão para acessar este recurso.", request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Handler para tentativas de login de usuários desabilitados.
     * Retorna um status HTTP 403 (Forbidden).
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Object> handleDisabledUser(DisabledException ex, HttpServletRequest request) {
        log.warn("Tentativa de login de usuário desabilitado na rota {}: {}", request.getRequestURI(), ex.getMessage());
        ApiExceptionResponse response = createErrorResponse(HttpStatus.FORBIDDEN, "Usuário inativo ou desabilitado.", request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Handler para erros de validação de parâmetros de método (ex: @RequestParam).
     * Retorna um status HTTP 400 (Bad Request).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(error -> {
            String fieldName = error.getPropertyPath().toString();
            String errorMessage = error.getMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("Erro de validação de constraint na rota {}: {}", request.getRequestURI(), errors);
        return new ResponseEntity<>(createErrorResponseWithDetails(HttpStatus.BAD_REQUEST, "Erro de validação", request.getRequestURI(), errors), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handler para erros de segurança ou comunicação com serviços externos (Google).
     * Retorna 500 Internal Server Error, mas com uma mensagem mais clara sobre a causa.
     */
    @ExceptionHandler({GeneralSecurityException.class, IOException.class})
    public ResponseEntity<Object> handleExternalServiceErrors(Exception ex, HttpServletRequest request) {
        log.error("Erro de segurança ou IO na rota {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ApiExceptionResponse response = createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro ao se comunicar com um serviço externo.", request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handler "Pega-Tudo" para qualquer outra exceção não tratada.
     * É a última linha de defesa para evitar que stack traces vazem para o cliente.
     * Retorna um status HTTP 500 (Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(Exception ex, HttpServletRequest request) {
        log.error("ERRO INESPERADO NA APLICAÇÃO na rota {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ApiExceptionResponse response = createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno inesperado no servidor.", request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * SOBRESCREVE o handler para erros de validação de corpo de requisição (@Valid).
     * Isso resolve a ambiguidade que impedia a aplicação de iniciar.
     * Retorna um status HTTP 400 (Bad Request).
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        log.error("Erro de validação de argumento: {}", errors);

        // Usa o método auxiliar para criar uma resposta padronizada com detalhes
        String requestURI = request.getDescription(false).substring(4); // Extrai a URI da WebRequest
        return new ResponseEntity<>(createErrorResponseWithDetails(HttpStatus.BAD_REQUEST, "Erro de validação", requestURI, errors), HttpStatus.BAD_REQUEST);
    }

    // --- MÉTODOS DE APOIO PARA CRIAR RESPOSTAS PADRONIZADAS ---

    private ApiExceptionResponse createErrorResponse(HttpStatus status, String message, String path) {
        return ApiExceptionResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
    }

    private ApiExceptionResponse createErrorResponseWithDetails(HttpStatus status, String message, String path, Map<String, String> details) {
        ApiExceptionResponse response = createErrorResponse(status, message, path);
        response.setDetails(details);
        return response;
    }
}
