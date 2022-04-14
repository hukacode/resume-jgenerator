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

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import dev.huka.resumejgenerator.common.Output;
import dev.huka.resumejgenerator.port.in.ResumeGeneratorCommand;
import dev.huka.resumejgenerator.port.out.ResumeProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
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
    try {
      var resume = resumeProvider.readResume();

      if (Strings.isBlank(resume.getMeta().getTheme())) {
        resume.getMeta().setTheme("simple");
      }
      if (Strings.isNotBlank(input.getTheme())) {
        resume.getMeta().setTheme(input.getTheme());
      }

      var ctx = new Context(Locale.US);
      ctx.setVariable("resume", resume);
      var outputPath = Path.of("output");
      var themePath = Path.of("themes", resume.getMeta().getTheme());
      FileSystemUtils.deleteRecursively(outputPath);

      var htmlString =
          this.springTemplateEngine.process(resume.getMeta().getTheme() + "/resume.html", ctx);
      if (input.isGenerateHTML()) {
        FileSystemUtils.copyRecursively(themePath, outputPath);
        Files.writeString(
            Path.of("output", "resume.html"),
            htmlString,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING);
        log.info("Create HTML file");
      }
      if (input.isGeneratePDF()) {
        try (var outputStream = new FileOutputStream(new File("output/resume.pdf"))) {
          var properties = new ConverterProperties();
          properties.setBaseUri(
              Path.of("themes", resume.getMeta().getTheme()).toAbsolutePath().toString());
          HtmlConverter.convertToPdf(htmlString, outputStream, properties);
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return Output.NOTHING;
  }
}
