package br.com.devcave.mybank.controller.handler;

import br.com.devcave.mybank.exception.MyBankException;
import brave.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class AdviceController {

    private final Tracer tracer;

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public HttpEntity<?> handlerMethodArgumentNotValidException(final MethodArgumentNotValidException ex,
                                                                final ServletWebRequest webRequest)
            throws MethodArgumentNotValidException {
        log.info("handlerMethodArgumentNotValidException");

        if (ex.getBindingResult().getAllErrors().size() == 0) {
            log.info("handlerMethodArgumentNotValidException, no errorlist, message={}", ex.getMessage());
            throw ex;
        }

        List<String> errorList = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(e -> getField(e) + ": " + e.getDefaultMessage())
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(new ErrorResponse(
                        tracer.currentSpan().context().traceIdString(),
                        errorList,
                        webRequest.getRequest().getServletPath()));
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public HttpEntity<?> handlerConstraintViolationException(final ConstraintViolationException ex,
                                                             final ServletWebRequest webRequest) {
        log.info("handlerConstraintViolationException");

        if (ex.getConstraintViolations() == null) {
            log.info("handlerConstraintViolationException, no errorlist, message={}", ex.getMessage());
            throw ex;
        }
        List<String> messages = ex.getConstraintViolations().stream()
                .map(e -> StreamSupport.stream(e.getPropertyPath().spliterator(), false)
                        .filter(m -> !ElementKind.METHOD.equals(m.getKind()))
                        .map(Path.Node::getName).reduce((a, b) -> a + "." + b).get() + ": " + e.getMessage())
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(new ErrorResponse(
                        tracer.currentSpan().context().traceIdString(),
                        messages,
                        webRequest.getRequest().getServletPath()));
    }

    @ExceptionHandler(value = BindException.class)
    public HttpEntity<?> handlerBindException(final BindException ex,
                                              final ServletWebRequest webRequest) throws BindException {
        log.info("handlerBindException");

        if (!ex.getBindingResult().hasErrors()) {
            log.info("handlerBindException, no errorlist, message={}", ex.getMessage());
            throw ex;
        }
        List<String> messages = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(new ErrorResponse(
                        tracer.currentSpan().context().traceIdString(),
                        messages,
                        webRequest.getRequest().getServletPath()));
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public HttpEntity<?> handlerHttpMessageNotReadableException(final HttpMessageNotReadableException ex,
                                                                final ServletWebRequest webRequest) {
        log.info("handlerHttpMessageNotReadableException", ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(new ErrorResponse(tracer.currentSpan().context().traceIdString(),
                        Collections.singletonList(ex.getMessage()),
                        webRequest.getRequest().getServletPath()));
    }

    @ExceptionHandler(value = MyBankException.class)
    public HttpEntity<?> handlerMyBankException(final MyBankException ex,
                                                final ServletWebRequest webRequest) {
        log.info("handlerMyBankException", ex);

        final List<String> errorList = List.of(ex.getMessage());

        return ResponseEntity
                .status(ex.getStatus().value())
                .body(new ErrorResponse(tracer.currentSpan().context().traceIdString(),
                        errorList,
                        webRequest.getRequest().getServletPath()));
    }

    @ExceptionHandler(value = Exception.class)
    public HttpEntity<?> handlerException(final Exception ex,
                                          final ServletWebRequest webRequest) {
        log.info("handlerException", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(new ErrorResponse(tracer.currentSpan().context().traceIdString(),
                        Collections.singletonList(ex.getMessage()),
                        webRequest.getRequest().getServletPath()));
    }

    private String getField(final ObjectError objectError) {
        if (objectError.getArguments() == null
                || objectError.getArguments().length == 0
                || !(objectError.getArguments()[0] instanceof DefaultMessageSourceResolvable)) {
            return objectError.getCode();
        }
        return ((DefaultMessageSourceResolvable) objectError.getArguments()[0]).getDefaultMessage();
    }
}

