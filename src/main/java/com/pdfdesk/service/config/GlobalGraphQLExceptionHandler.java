package com.pdfdesk.service.config;

import com.pdfdesk.service.auth.exception.AuthException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import org.jspecify.annotations.NonNull;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GlobalGraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

  @Override
  protected GraphQLError resolveToSingleError(@NonNull Throwable ex, graphql.schema.@NonNull DataFetchingEnvironment env) {

    if (ex instanceof AuthException authEx) {
      return GraphqlErrorBuilder.newError()
              .message(authEx.getMessage())
              .path(env.getExecutionStepInfo().getPath())
              .extensions(Map.of("code", authEx.getCode()))
              .build();
    }

    return GraphqlErrorBuilder.newError()
            .message("Internal server error")
            .extensions(Map.of("code", "INTERNAL_ERROR"))
            .build();
  }
}
