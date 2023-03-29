package org.main.il;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class Program {
    private List<Instruction> instructions = new ArrayList<>();

    public void generate(String filename) throws IOException {
        StringBuilder builder = new StringBuilder();
        this.instructions.forEach(x -> {
            builder.append(x.toString());
        });
        Files.writeString(Path.of(filename), builder.toString());
    }
}
