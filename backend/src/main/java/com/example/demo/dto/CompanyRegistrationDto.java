package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompanyRegistrationDto {
    private String categoryId;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "유효한 이메일 형식이 아닙니다")
    @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    private String password;

    @NotBlank(message = "사용자 이름은 필수입니다")
    @Size(max = 50, message = "사용자 이름은 50자를 초과할 수 없습니다")
    private String userName;

    @NotBlank(message = "전화번호는 필수입니다")
    @Size(max = 50, message = "전화번호는 50자를 초과할 수 없습니다")
    private String userPhone;

    @Size(max = 50, message = "닉네임은 50자를 초과할 수 없습니다")
    private String nickname;

    @NotBlank(message = "회사명은 필수입니다")
    @Size(max = 100, message = "회사명은 100자를 초과할 수 없습니다")
    private String companyName;

    private String profileAttachmentUrl;

    @NotBlank(message = "사업자 번호는 필수입니다")
    private String businessNumber;

    private String homepage;
}
