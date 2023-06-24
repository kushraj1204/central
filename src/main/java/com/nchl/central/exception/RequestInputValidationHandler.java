package com.nchl.central.exception;//package com.nchl.npibanks.exception;
//
//import com.google.common.base.CaseFormat;
//import com.nchl.npibanks.model.system.response.ErrorField;
//import com.nchl.npibanks.model.system.response.MessageErrorNode;
//import com.nchl.npibanks.model.system.response.ResponseData;
//import org.springframework.context.support.DefaultMessageSourceResolvable;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@RestControllerAdvice
//public class RequestInputValidationHandler extends ResponseEntityExceptionHandler {
//
//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
//                                                                  HttpHeaders headers,
//                                                                  HttpStatusCode status,
//                                                                  WebRequest request) {
//        Exception exception = new Exception(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
//        List<String> errorMessages = new ArrayList<>();
//        BindingResult bindingResult = ex.getBindingResult();
//        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
//        for (FieldError error : fieldErrors) {
//            String message = error.getField() + " - "
//                    + error.getDefaultMessage() + " - "
//                    + error.getRejectedValue();
//            errorMessages.add(message);
//        }
//
//        List<ErrorField> fields = new ArrayList<>();
//        Map<String, List<FieldError>> errorMap = fieldErrors.stream().collect(Collectors.groupingBy(FieldError::getField));
//        errorMap.forEach((key, value) -> fields.add(ErrorField.builder()
//                .field(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, key))
//                .messages(value.stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
//                        .toList())
//                .build()));
//
//        return this.createResponseEntity(HttpStatus.BAD_REQUEST, exception, request, fields);
//    }
//
//    private ResponseEntity<Object> createResponseEntity(HttpStatus httpStatus,
//                                                        Exception ex,
//                                                        WebRequest request,
//                                                        List<ErrorField> errorFields) {
//        //boolean concatResponse = Optional.of(Boolean.parseBoolean(request.getParameter("concat_response"))).orElse(false);
//        boolean concatResponse = true;
//        ResponseData.ResponseDataBuilder errorResponse = ResponseData.builder()
//                .message("Issue validating message request.");
//        if (concatResponse) {
//            String message = errorFields.stream().map(x -> x.getMessages()
//                    .stream()
//                    .map(y -> "Field " + x.getField() + " " + y)
//                    .collect(Collectors.joining(". "))
//            ).collect(Collectors.joining(". "));
//            errorResponse.message("Issue validating message request: " + message);
//            errorResponse.status(false);
//            errorResponse.code("400");
//        } else {
//            errorResponse.errors(MessageErrorNode.builder().fields(errorFields).build());
//        }
//        return handleExceptionInternal(ex, errorResponse.build(),
//                new HttpHeaders(), httpStatus, request);
//    }
//
//
//}