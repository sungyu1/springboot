package jpabook.jpashop.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
//  회원가입 폼으로 이동
    @GetMapping(value = "/members/new")
    public String createForm(Model model){
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    // 회원가입 폼으로 이동 (새로운 경로)
//    @GetMapping(value = "/members/createMemberForm")
//    public String createMemberForm(Model model){
//        model.addAttribute("memberForm", new MemberForm());
//        return "members/createMemberForm";
//    }
//  회원가입 처리
    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        Member member = new Member();
        member.setId(form.getId());
        member.setName(form.getName());
        member.setPassword(form.getPassword());
        member.setPhone(form.getPhone());
        member.setAddress(form.getAddress());
        member.setDeptCode(form.getDeptCode());
        member.setJobLevel(form.getJobLevel());
        member.setUseFlag("1"); // 자동으로 근무 상태로 설정
        
        // 기본값 설정
        member.setDefaultValues();
        
        // 서명 이미지 처리 (Base64를 BLOB으로 변환 및 파일 저장)
        if (form.getSignatureData() != null && !form.getSignatureData().isEmpty()) {
            try {
                // Base64 문자열에서 "data:image/...;base64," 부분 제거
                String base64Data = form.getSignatureData();
                if (base64Data.contains(",")) {
                    base64Data = base64Data.substring(base64Data.indexOf(",") + 1);
                }
                
                // Base64를 byte 배열로 변환
                byte[] signatureBytes = java.util.Base64.getDecoder().decode(base64Data);
                member.setSignatureImage(signatureBytes);
                
                // 서명 이미지를 파일로 저장
                saveSignatureToFile(member.getId(), signatureBytes);
                
            } catch (Exception e) {
                // 서명 이미지 변환 실패 시 null로 설정
                member.setSignatureImage(null);
            }
        }

        try {
            memberService.join(member);
        } catch (IllegalStateException e) {
            result.rejectValue("id", "duplicate", e.getMessage()); // ← id 필드에 바인딩된 에러 추가
            return "members/createMemberForm"; // 다시 회원가입 폼으로 이동
        }

        return "redirect:/";
    }
//  전체회원 목록조회
    @GetMapping("/members/memberList")
    public String list(Model model){
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
// 아이디 중복체크
    @GetMapping("/members/check-id")
    @ResponseBody
    public String checkIdDuplicate(@RequestParam("id") String id) {
        boolean available = memberService.isIdAvailable(id);
        return available ? "AVAILABLE" : "DUPLICATE";
    }

    // 서명 이미지 조회
    @GetMapping("/members/signature/{memberId}")
    @ResponseBody
    public ResponseEntity<byte[]> getSignatureImage(@PathVariable String memberId) {
        Member member = memberService.findOne(memberId);
        if (member == null || member.getSignatureImage() == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(member.getSignatureImage());
    }

    // 서명 이미지를 파일로 저장하는 메서드
    private void saveSignatureToFile(String memberId, byte[] signatureBytes) {
        try {
            // 서명 저장 디렉토리 생성
            String signDirectory = "C:\\Users\\user\\Desktop\\signs";
            Path directoryPath = Paths.get(signDirectory);
            
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }
            
            // 파일명 생성 (memberId_signature.png)
            String fileName = memberId + "_signature.png";
            Path filePath = directoryPath.resolve(fileName);
            
            // 파일로 저장
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                fos.write(signatureBytes);
            }
            
            System.out.println("서명 이미지가 저장되었습니다: " + filePath.toString());
            
        } catch (IOException e) {
            System.err.println("서명 이미지 저장 중 오류 발생: " + e.getMessage());
        }
    }

}
