package id.ac.ui.cs.advprog.gatra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Ambil header Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 2. Cek apakah header ada dan berawalan "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Lanjut ke filter berikutnya (akan ditolak nanti jika butuh login)
            return;
        }

        // 3. Ekstrak token (potong kata "Bearer " di depannya)
        jwt = authHeader.substring(7);
        username = jwtUtil.extractUsername(jwt); // Ambil username dari dalam token

        // 4. Jika token ada username-nya dan user belum di-autentikasi di konteks saat ini
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Ambil data user dari database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Cek apakah tokennya valid sesuai data user
            // Catatan: Karena JwtUtil kita memakai parameter model User, pastikan method ini disesuaikan
            // atau ubah JwtUtil untuk menerima UserDetails (saya rekomendasikan ubah JwtUtil agar lebih standar)
            if (jwtUtil.isTokenValid(jwt, userDetails)) {

                // Buat token autentikasi standar Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Simpan status "Sudah Login" ke dalam konteks Spring
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
