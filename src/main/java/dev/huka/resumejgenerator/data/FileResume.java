/*
 * The MIT License
 * Copyright © 2022 huka.dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dev.huka.resumejgenerator.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.huka.resumejgenerator.domain.Resume;
import dev.huka.resumejgenerator.port.out.ResumeProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FileResume implements ResumeProvider {
  private ObjectMapper objectMapper;

  @Override
  public Resume readResume() {
    var resumeJson = "resume.json";
    try {
      return objectMapper.readValue(
          new File(Path.of(System.getProperty("user.dir"), resumeJson).toAbsolutePath().toString()),
          Resume.class);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
}
