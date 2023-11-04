package utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import javafx.scene.image.Image;

public final class Resources {
    /** {@code Map}contenant les paths aux fonts. */
    private static Map<String, String> ttf = new HashMap<>();
    /** {@code Map}contenant les paths aux mp4. */
    private static Map<String, String> mp4 = new HashMap<>();
    /** {@code Map}contenant les paths aux wav. */
    private static Map<String, String> wav = new HashMap<>();
    /** {@code Map}contenant les objets{@code Image}. */
    private static Map<String, Image> png = new HashMap<>();
    /** {@code Map}contenant les fonts. */
    private static List<Map<String, String>> all = new ArrayList<>();

    private Resources() {
    }

    static {
        all.add(ttf);
        all.add(mp4);
        all.add(wav);
        try {
            Files.list(
                Path.of("src/main/resources/fonts")).forEach(Resources::sort
            );
            Files.list(
                Path.of("src/main/resources/mp4")).forEach(Resources::sort
            );
            Files.list(
                Path.of("src/main/resources/png")).forEach(Resources::sort
            );
            Files.list(
                Path.of("src/main/resources/wav")).forEach(Resources::sort
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sort(final Path file) {
        switch (FilenameUtils.getExtension(file.toString())) {
            case "ttf":
                ttf.put(
                    FilenameUtils.removeExtension(
                        file.getFileName().toString()
                    ),
                    Paths.get(file.toString()).toUri().toString()
                );
                break;
            case "mp4":
                mp4.put(
                    FilenameUtils.removeExtension(
                        file.getFileName().toString()
                    ),
                    Paths.get(file.toString()).toUri().toString()
                );
                break;
            case "png":
                png.put(
                    FilenameUtils.removeExtension(
                        file.getFileName().toString()
                    ),
                    new Image(Paths.get(file.toString()).toUri().toString())
                );
                break;
            case "wav":
                wav.put(
                    FilenameUtils.removeExtension(
                        file.getFileName().toString()
                    ),
                    Paths.get(file.toString()).toUri().toString()
                );
                break;

            default:
                break;
        }
    }

    /**
     * Retourne le path du fichier.
     * @param name Nom du fichier
     * @return {@code String}path
     */
    public static String getFile(final String name) {
        for (Map<String, String> map : all) {
            if (map.containsKey(name)) {
                return map.get(name);
            }
        }
        return "";
    }

    /**
     * Retourne l'objet{@code Image}correspondant au nom.
     * @param name Nom de l'image
     * @return {@code Image}
     */
    public static Image getImage(final String name) {
        return png.get(name);
    }
}
