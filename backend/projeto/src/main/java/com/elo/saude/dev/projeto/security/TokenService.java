package com.elo.saude.dev.projeto.security;

import com.elo.saude.dev.projeto.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class TokenService {

    private final JwtEncoder encoder;
    private final Duration validade;

    public TokenService(JwtEncoder encoder,
                        @Value("${app.security.jwt.expiration-minutes}") long minutos) {
        this.encoder = encoder;
        this.validade = Duration.ofMinutes(minutos);
    }

    public String gerar(Usuario u) {
        Instant agora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("elosaude")
                .subject(u.getEmail())
                .issuedAt(agora)
                .expiresAt(agora.plus(validade))
                .claim("uid", u.getId())
                .claim("perfil", u.getPerfil().name())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long validadeEmSegundos() {
        return validade.toSeconds();
    }
}
