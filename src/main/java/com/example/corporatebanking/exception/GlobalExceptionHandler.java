package com.example.corporatebanking.exception;

import com.example.corporatebanking.dto.ApiErrorResponse;
import com.example.rbcaccess.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Single place where every RbcAuthorizationException subtype thrown by
 * rbc-access is translated into the JSON error shape required by the spec:
 *
 * <pre>
 * {
 *   "status": "DENIED",
 *   "code": "ACCESS_DENIED",
 *   "message": "..."
 * }
 * </pre>
 *
 * Corporate Banking controllers/services never catch these exceptions
 * individually - they simply let them propagate up to here.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // -------- RBC authorization failures (any subtype) --------

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return denied(HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler(TransactionLimitExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleTransactionLimit(TransactionLimitExceededException ex) {
        return denied(HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler(ApprovalLimitExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleApprovalLimit(ApprovalLimitExceededException ex) {
        return denied(HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler(SelfApprovalNotAllowedException.class)
    public ResponseEntity<ApiErrorResponse> handleSelfApproval(SelfApprovalNotAllowedException ex) {
        return denied(HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return denied(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(UserInactiveException.class)
    public ResponseEntity<ApiErrorResponse> handleUserInactive(UserInactiveException ex) {
        return denied(HttpStatus.FORBIDDEN, ex);
    }

    // Fallback for any future RbcAuthorizationException subtype not listed above.
    @ExceptionHandler(RbcAuthorizationException.class)
    public ResponseEntity<ApiErrorResponse> handleGenericRbc(RbcAuthorizationException ex) {
        return denied(HttpStatus.FORBIDDEN, ex);
    }

    private ResponseEntity<ApiErrorResponse> denied(HttpStatus httpStatus, RbcAuthorizationException ex) {
        ApiErrorResponse body = ApiErrorResponse.builder()
                .status("DENIED")
                .code(ex.getReasonCode())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(httpStatus).body(body);
    }

    // -------- Corporate Banking's own exceptions --------

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ApiErrorResponse body = ApiErrorResponse.builder()
                .status("ERROR")
                .code("NOT_FOUND")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalState(IllegalStateException ex) {
        ApiErrorResponse body = ApiErrorResponse.builder()
                .status("ERROR")
                .code("INVALID_STATE")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<ApiErrorResponse> handleValidation(Exception ex) {
        ApiErrorResponse body = ApiErrorResponse.builder()
                .status("ERROR")
                .code("VALIDATION_FAILED")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ApiErrorResponse body = ApiErrorResponse.builder()
                .status("ERROR")
                .code("INVALID_PARAMETER")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex) {
        ApiErrorResponse body = ApiErrorResponse.builder()
                .status("ERROR")
                .code("INTERNAL_ERROR")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
