package org.simsimhi.models.member;

import lombok.RequiredArgsConstructor;
import org.simsimhi.api.controllers.members.RequestLogin;
import org.simsimhi.config.jwt.TokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberLoginService {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public String login(RequestLogin form){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(form.email(),form.password());
        Authentication authentication =authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        String accessToken =tokenProvider.createToken(authentication); //JWT 토큰 생성
        
        return accessToken;
    }
}
