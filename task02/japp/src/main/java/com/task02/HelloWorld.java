package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.util.Map;

@LambdaHandler(
    lambdaName = "hello_world",
	roleName = "hello_world-role",
	isPublishVersion = true,
	aliasName = "${lambdas_alias_name}",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(
	authType = AuthType.NONE,
	invokeMode = InvokeMode.BUFFERED
)
public class HelloWorld implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
		Map<String, Object> http = extractHttp(event);
		String method = String.valueOf(http.getOrDefault("method", ""));
		String path = String.valueOf(http.getOrDefault("path", ""));

		if ("GET".equals(method) && "/hello".equals(path)) {
			return response(200, "Hello from Lambda");
		}
		return response(400, "Bad request syntax or unsupported method. Request path: "
				+ path + ". HTTP method: " + method);
	}

	// Function URL event: { "requestContext": { "http": { "method": "GET", "path": "/hello", ... } } }
	@SuppressWarnings("unchecked")
	private Map<String, Object> extractHttp(Map<String, Object> event) {
		if (event == null || !(event.get("requestContext") instanceof Map)) {
			return Map.of();
		}
		Map<String, Object> requestContext = (Map<String, Object>) event.get("requestContext");
		Object http = requestContext.get("http");
		return http instanceof Map ? (Map<String, Object>) http : Map.of();
	}

	// statusCode sets the HTTP status; body is what the client actually receives
	private Map<String, Object> response(int statusCode, String message) {
		String body = "{\"statusCode\": " + statusCode + ", \"message\": \"" + escapeJson(message) + "\"}";
		return Map.of(
				"statusCode", statusCode,
				"headers", Map.of("Content-Type", "application/json"),
				"body", body
		);
	}

	private String escapeJson(String value) {
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}
