package com.example.graalvmspringbootvuedemo.controller;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Controller
public class ViewController {

    @GetMapping("/")
    public Mono<String> index(final Model model) throws IOException {
        model.addAttribute("renderedHtml", this.renderToString("/"));
        return Mono.just("index");
    }

    @GetMapping("/about")
    public Mono<String> about(final Model model) throws IOException {
        model.addAttribute("renderedHtml", this.renderToString("/about"));
        return Mono.just("index");
    }

    private String renderToString(String urlPath) throws IOException {
        // ## js context polyglot
        final String fileName = "server.js";
        final String initScript = "RENDERED({url: '" + urlPath + "'}).then(v => { renderedHtml = v })";
        try (Context context = Context.newBuilder("js").allowIO(true).build()) {
            context.getBindings("js").putMember("renderedHtml", "");
            context.eval(Source.newBuilder("js", "load('src/main/resources/" + fileName + "')", "renderer.js").build());
            context.eval(Source.newBuilder("js", initScript, "renderer.js").build());
            Value renderedHtml = context.getBindings("js").getMember("renderedHtml");
            return renderedHtml.toString();
        }
    }

}
