package com.pdfdesk.service.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.jspecify.annotations.NonNull;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

@Component
@Order(-1)
public class GraphQlErrorResponseFilter extends OncePerRequestFilter {

  private static final String GRAPHQL_PATH = "/graphql";
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
    if (!GRAPHQL_PATH.equals(request.getRequestURI())) {
      filterChain.doFilter(request, response);
      return;
    }

    ByteArrayResponseWrapper wrapper = new ByteArrayResponseWrapper(response);
    filterChain.doFilter(request, wrapper);

    byte[] body = wrapper.getContent();
    if (body.length == 0) {
      return;
    }

    String json = new String(body, StandardCharsets.UTF_8);
    try {
      JsonNode root = objectMapper.readTree(json);
      JsonNode errors = root != null ? root.get("errors") : null;
      if (errors == null || !errors.isArray() || errors.isEmpty()) {
        response.getOutputStream().write(body);
        return;
      }

      JsonNode first = errors.get(0);
      String message = first.has("message") ? first.get("message").asText() : "Error";
      String code = "ERROR";
      if (first.has("extensions") && first.get("extensions").has("code")) {
        code = first.get("extensions").get("code").asText();
      }

      response.resetBuffer();
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setCharacterEncoding(StandardCharsets.UTF_8.name());
      String out = objectMapper.writeValueAsString(new ErrorBody(code, message));
      response.getOutputStream().write(out.getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      response.getOutputStream().write(body);
    }
  }

  private record ErrorBody(String code, String message) {}

  private static final class ByteArrayResponseWrapper extends HttpServletResponseWrapper {
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private PrintWriter writer;

    ByteArrayResponseWrapper(HttpServletResponse response) {
      super(response);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
      if (writer == null) {
        writer = new PrintWriter(new Writer() {
          @Override
          public void write(char @NonNull [] buff, int off, int len) throws IOException {
            buffer.write(new String(buff, off, len).getBytes(StandardCharsets.UTF_8));
          }

          @Override
          public void flush() {}

          @Override
          public void close() {}
        });
      }
      return writer;
    }

    @Override
    public jakarta.servlet.ServletOutputStream getOutputStream() throws IOException {
      return new jakarta.servlet.ServletOutputStream() {
        @Override
        public boolean isReady() { return true; }
        @Override
        public void setWriteListener(jakarta.servlet.WriteListener listener) {}
        @Override
        public void write(int b) { buffer.write(b); }
        @Override
        public void write(byte @NonNull [] b, int off, int len) { buffer.write(b, off, len); }
      };
    }

    byte[] getContent() throws IOException {
      if (writer != null) writer.flush();
      return buffer.toByteArray();
    }
  }
}
