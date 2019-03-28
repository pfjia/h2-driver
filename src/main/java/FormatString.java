import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author pfjia
 * @since 2019/3/1 15:17
 */
public class FormatString {
    public static void main(String[] args) throws IOException, URISyntaxException {
        Path formatPath = Paths.get("src/main/resources/format.txt");

        URL url = Thread.currentThread().getContextClassLoader().getResource("tmp.txt");
        assert url != null;
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path);
        lines = lines.stream().map(new Function<String, String>() {
            @Override
            public String apply(String s) {
                if (s.endsWith(".")) {
                    return s + "\n";
                } else {
                    return s +" ";
                }
            }
        }).collect(Collectors.toList());
        String s = String.join("", lines);
        Files.writeString(formatPath, s);
        System.out.println(s);

    }
}
