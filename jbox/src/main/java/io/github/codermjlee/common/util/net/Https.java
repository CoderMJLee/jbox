package io.github.codermjlee.common.util.net;

import io.github.codermjlee.common.util.Jsons;
import io.github.codermjlee.common.util.io.Ios;
import okhttp3.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Https {
    private static final OkHttpClient CLIENT;
    private static final int TIME_OUT = 10;
    static {
        CLIENT = new OkHttpClient().newBuilder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(32, 5, TimeUnit.MINUTES))
                .build();
    }

    public enum Method {
        GET, POST
    }

    private String url;
    private Method method = Method.GET;
    private Map<String, Object> params;
    private Map<String, String> headers;
    private Completion completion;
    // 文件上传
    private File file;
    private String fileKey;
    private String filename;
    // JSON
    private boolean json;
    private String jsonParams;

    public Https json(boolean json) {
        this.json = json;
        this.method = Method.POST;
        return this;
    }

    public Https jsonParams(String jsonParams) {
        this.jsonParams = jsonParams;
        return json(true);
    }

    public Https jsonParams(Object jsonParams) {
        return jsonParams(Jsons.getString(jsonParams));
    }

    public Https url(String url) {
        this.url = fullUrl(url);
        return this;
    }

    public Https method(Method method) {
        this.method = method;
        return this;
    }

    public Https file(String filepath) {
        return file(new File(filepath));
    }

    public Https file(File file) {
        this.file = file;
        this.method = Method.POST;
        this.filename = file.getName();
        return this;
    }

    public Https fileKey(String fileKey) {
        this.fileKey = fileKey;
        return this;
    }

    public Https filename(String filename) {
        this.filename = filename;
        return this;
    }

    public Https params(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    public Https addParam(String k, Object v) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(k, v);
        return this;
    }

    public Https headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public Https addHeader(String k, String v) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(k, v);
        return this;
    }

    public Https completion(Completion completion) {
        this.completion = completion;
        return this;
    }

    public static Https alloc() {
        return new Https();
    }

    public static String fullUrl(String uri) {
        if (uri.startsWith("http:") || uri.startsWith("https:")) {
            return uri;
        } else {
            return "http://" + uri;
        }
    }

    public void download(String filepath, DownloadCompletion completion) {
        if (filepath == null || completion == null) return;

        this.completion = new Completion() {
            @Override
            public void success(Response response) {
                // 检查文件是否存在
                File file = new File(filepath);
                // 如果有相同大小的文件存在，就直接结束
                boolean exists = file.exists() && file.length() == response.length();
                if (completion.end(response, exists)) return;

                try {
                    Ios.alloc()
                            .inData(response.bytes())
                            .outFile(file)
                            .writeBytes();
                    completion.success(response);
                } catch (Exception e) {
                    completion.error(e);
                }
            }

            @Override
            public void error(Exception exception) {
                completion.error(exception);
            }
        };
    }

    private Https() {

    }

    public void async() {
        if (completion == null) return;

        try {
            call().enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    completion.error(e);
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    try (Response resp = new Response(response)) {
                        completion.success(resp);
                    }
                }
            });
        } catch (Exception e) {
            completion.error(e);
        }
    }

    public Response sync() throws Exception {
        return new Response(call().execute());
    }

    public String syncString() throws Exception {
        return sync().string();
    }

    private Call call() throws Exception {
        // 创建一个Request.Builder
        Request.Builder builder = new Request.Builder();

        // 创建一个Request
        Request request = builder.url(url).build();

        // 请求参数
        switch (method) {
            case GET: {
                if (params != null) {
                    // 创建一个HttpUrl.Builder
                    HttpUrl.Builder urlBuilder = request.url().newBuilder();
                    params.forEach((k, v) -> {
                        urlBuilder.addQueryParameter(k, v.toString());
                    });
                    builder.url(urlBuilder.build());
                }
                break;
            }
            case POST: {
                RequestBody body = null;
                if (file != null) { // 文件上传
                    // 文件长度
                    long len;
                    try (FileInputStream fis = new FileInputStream(file)) {
                        len = fis.available();
                    }
                    MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
                    bodyBuilder.setType(MultipartBody.FORM);
                    bodyBuilder.addFormDataPart(fileKey, filename,
                            RequestBody.create(file, MultipartBody.FORM));
                    if (params != null) {
                        params.forEach((k, v) -> {
                            bodyBuilder.addFormDataPart(k, v.toString());
                        });
                    }
                    body = bodyBuilder.build();
                } else if (json) {
                    String text = (jsonParams != null) ? jsonParams : Jsons.getString(params);
                    if (text != null) {
                        body = RequestBody.create(text, MediaType.parse("application/json"));
                    }
                } else if (params != null) {
                    // 创建一个FormBody.Builder
                    FormBody.Builder bodyBuilder = new FormBody.Builder();
                    params.forEach((k, v) -> {
                        bodyBuilder.add(k, v.toString());
                    });
                    body = bodyBuilder.build();
                }
                if (body != null) {
                    builder.post(body);
                }
                break;
            }
        }

        // 请求头
        if (headers != null) {
            // 创建一个Headers.Builder
            Headers.Builder headerBuilder = request.headers().newBuilder();
            headers.forEach(headerBuilder::add);
            builder.headers(headerBuilder.build());
        }

        return CLIENT.newCall(builder.build());
    }

    public static class Response implements AutoCloseable {
        private final okhttp3.Response response;
        private final ResponseBody responseBody;
        private Response(okhttp3.Response response) {
            this.response = response;
            this.responseBody = response.body();
        }
        public String url() {
            return response.request().url().toString();
        }
        public int code() {
            return response.code();
        }
        public long length() {
            return responseBody.contentLength();
        }
        public InputStream inputStream() {
            return responseBody.byteStream();
        }
        public byte[] bytes() throws Exception {
            return responseBody.bytes();
        }
        public String string() {
            return string(StandardCharsets.UTF_8);
        }
        public String string(Charset charset) {
            try {
                return new String(bytes(), charset);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        public String header(String name) {
            return response.header(name);
        }
        public List<String> headers(String name) {
            return response.headers(name);
        }

        @Override
        public void close() {
            if (response != null) {
                response.close();
            }
            if (responseBody != null) {
                responseBody.close();
            }
        }
    }

    public interface DownloadCompletion extends Completion {
        default boolean end(Response response, boolean exists) {
            return exists;
        }
    }

    public interface Completion {
        void success(Response response);
        void error(Exception exception);
    }
}
