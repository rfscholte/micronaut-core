/*
 * Copyright 2017 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.docs.server.upload;

// tag::imports[]
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.StreamingFileUpload;
import io.reactivex.Single;
import org.reactivestreams.Publisher;

import java.util.Optional;
// end::imports[]

// tag::completedImports[]
import io.micronaut.http.server.netty.multipart.CompletedFileUpload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
// end::completedImports[]
/**
 * @author Graeme Rocher
 * @since 1.0
 */
// tag::class[]
@Controller
public class UploadController {
// end::class[]

    // tag::upload[]
    @Post(value = "/", consumes = MediaType.MULTIPART_FORM_DATA) // <1>
    public Single<HttpResponse<String>> upload(StreamingFileUpload file, Optional<String> anotherAttribute) { // <2>
        Publisher<Boolean> uploadPublisher = file.transferTo(file.getFilename()); // <3>
        return Single.fromPublisher(uploadPublisher)  // <4>
                    .map(success -> {
                        if (success) {
                            return HttpResponse.ok("Uploaded");
                        } else {
                            return HttpResponse.<String>status(HttpStatus.CONFLICT)
                                               .body("Upload Failed");
                        }
                    });
    }
    // end::upload[]

    // tag::completedUpload[]
    @Post(value = "/completed", consumes = MediaType.MULTIPART_FORM_DATA) // <1>
    public HttpResponse<String> uploadCompleted(CompletedFileUpload file, Optional<String> anotherAttribute) { // <2>

        try {
            Path path = Paths.get(file.getFilename()); //<3>
            Files.write(path, file.getBytes());

            return HttpResponse.ok("Uploaded");
        } catch (IOException exception) {
            return HttpResponse.badRequest("Upload Failed");
        }
    }
    // end::completedUpload[]

    // tag::bytesUpload[]
    @Post(value = "/bytes", consumes = MediaType.MULTIPART_FORM_DATA) // <1>
    public HttpResponse<String> uploadBytes(byte[] file, String fileName) { // <2>

        try {
            Path path = Paths.get(fileName);
            Files.write(path, file); // <3>

            return HttpResponse.ok("Uploaded");
        } catch (IOException exception) {
            return HttpResponse.badRequest("Upload Failed");
        }
    }
    // end::bytesUpload[]

}
