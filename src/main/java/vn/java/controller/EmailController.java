package vn.java.controller;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.java.dto.response.DataResponse;
import vn.java.exception.ErrorResponse;
import vn.java.service.MailService;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;


@RestController
@Slf4j
@RequestMapping("common")
public record EmailController(MailService mailService) {

    @PostMapping("/send-email")
    public Object sendEmail(@RequestParam String recipients, @RequestParam String subject,
                                        @RequestParam String content, @RequestParam(required = false) MultipartFile[] files) {
        log.info("Request GET /common/send-email");
        try {
            return new DataResponse<>(ACCEPTED.value(), mailService.sendEmail(recipients, subject, content, files));
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error("Sending email was failure, message={}", e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setTimestamp(new Date());
            errorResponse.setPath("/common/send-email");
            errorResponse.setStatus(INTERNAL_SERVER_ERROR.value());
            errorResponse.setError(INTERNAL_SERVER_ERROR.getReasonPhrase());
            errorResponse.setMessage(e.getMessage());
            return errorResponse;
        }
    }

}
