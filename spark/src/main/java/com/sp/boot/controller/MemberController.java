package com.sp.boot.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sp.boot.dto.JwtToken;
import com.sp.boot.dto.LoginInfo;
import com.sp.boot.dto.MemberDto;
import com.sp.boot.service.MemberService;
import com.sp.boot.util.JwtProvider;

import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {
	

	private final MemberService memberService;
	private final JwtProvider jwtProvider;
	
	// 로그인
	
	@PostMapping("/login")
	public LoginInfo MemberLogin(@RequestBody MemberDto m) {		
		
	    // 1. 유저 확인
	    MemberDto memberDto = memberService.login(m);
	    
	    if(memberDto == null) {
	    	 throw new RuntimeException("아이디 또는 비밀번호가 틀렸습니다.");
	    }else {
	    	// 2. 토큰 생성
	    	String accessToken = jwtProvider.createToken(m.getMemId());
	    	//String refreshToken = jwtProvider.createRefreshToken(); // 필요하면
	    	
	    	
	    	// 리프레시 토큰 db에저장(예정)
	    	
	    	
	    	JwtToken token = JwtToken.builder()
	    			.grantType("Bearer")
	    			.accessToken(accessToken)
	    			//.refreshToken(refreshToken)
	    			.build();
	    	
	    	// 3. 토큰 + 사용자 정보 반환
	    	return new LoginInfo(token, memberDto);
	    	
	    }
	}
	
    @PostMapping("/refresh")
    public JwtToken reissue(@RequestHeader("Authorization") String refreshTokenHeader) { // 헤더에있는 Authorization 값 받아옴

        String refreshToken = refreshTokenHeader.replace("Bearer ", ""); // Bearer를 제거해 token만 추출

        if (!jwtProvider.validateToken(refreshToken)) { // 유효성 검사
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        String userId = jwtProvider.getUserId(refreshToken);
        String AccessToken = jwtProvider.createToken(userId);
        String newRefreshToken = jwtProvider.createRefreshToken(); // 새 refresh 발급

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(AccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
    
    
    @PostMapping("/validate")
    public MemberDto validate(@RequestHeader("Authorization") String authHeader) {
    	
    	String validateToken = authHeader.replace("Bearer", ""); // 토큰만 뺴내어
    	
        if (!jwtProvider.validateToken(validateToken)) { // 유효성 검사 진행
            throw new RuntimeException("토큰이 유효하지 않음");
        }
    	
    	String token = jwtProvider.getUserId(validateToken);
    	MemberDto memberDto = memberService.loginUserInfo(token);

    	
    	return memberDto;
    }
    
    
    
    @PostMapping
    public String sms(String phone) {
    	
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < num.length; i++) {
    	    sb.append(num[i]);
    	}
    	String code = sb.toString();

    	
    	DefaultMessageService messageService =  NurigoApp.INSTANCE.initialize("NCSERQEIBVBBZJKR", "NPEW4QVQX3KP5A5V7EQLJKJ8M7PHWOWO", "https://api.coolsms.co.kr");
    	// Message 패키지가 중복될 경우 net.nurigo.sdk.message.model.Message로 치환하여 주세요
    	Message message = new Message();
    	message.setFrom("01055106509");
    	message.setTo(phone);
    	message.setText("spark 인증 번호는 : [ "+ num +" ] 입니다");

    	try {
    	  // send 메소드로 ArrayList<Message> 객체를 넣어도 동작합니다!
    	  messageService.send(message);
    	} catch (NurigoMessageNotReceivedException exception) {
    	  // 발송에 실패한 메시지 목록을 확인할 수 있습니다!
    	  System.out.println(exception.getFailedMessageList());
    	  System.out.println(exception.getMessage());
    	} catch (Exception exception) {
    	  System.out.println(exception.getMessage());
    	}
    	
    	
    	return "발송 완료";
    	
    }
    
    
    
    
    
	
	
	

}
