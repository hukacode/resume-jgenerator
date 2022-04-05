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
package dev.huka.resumejgenerator.business;

import dev.huka.resumejgenerator.common.Output;
import dev.huka.resumejgenerator.port.in.ResumeGeneratorCommand;
import dev.huka.resumejgenerator.port.out.ResumeProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
@Slf4j
public class ResumeGeneratorHandler implements ResumeGeneratorCommand {
  private ResumeProvider resumeProvider;
  private TemplateEngine springTemplateEngine;

  @Override
  public Output execute(ResumeGeneratorInput input) {
    var resume = resumeProvider.readResume();

    var ctx = new Context(Locale.US);
    ctx.setVariable("resume", resume);

    var htmlContent = this.springTemplateEngine.process(input.getTheme() + "/index.html", ctx);
    if (input.isGenerateHTML()) {
      try {
        var outputPath = Path.of("output");
        var sourcePath = Path.of("themes", input.getTheme());
        FileSystemUtils.copyRecursively(outputPath, sourcePath);
        Files.writeString(
            Path.of("output", "index.html"),
            htmlContent,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING);
        log.info("Create HTML file");
      } catch (IOException e) {
        log.warn("Error occurred: " + e.getMessage(), e);
      }
    }
    return Output.NOTHING;
  }
}
