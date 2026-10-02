package net.mcreator.ffmpeglib;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

public class AdvancedMediaProcessor {

    public static CompletableFuture<File> extractAudioCustom(File ffmpegExe, File videoFile, File customOutputDir, String format) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!customOutputDir.exists()) {
                    customOutputDir.mkdirs();
                }
                
                String fileName = videoFile.getName();
                String baseName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
                File outputFile = new File(customOutputDir, baseName + "." + format);

                if (outputFile.exists()) {
                    return outputFile;
                }

                ProcessBuilder pb = new ProcessBuilder(
                        ffmpegExe.getAbsolutePath(),
                        "-y",
                        "-i", videoFile.getAbsolutePath(),
                        "-vn",
                        "-acodec", getCodecForFormat(format),
                        outputFile.getAbsolutePath()
                );
                
                pb.redirectErrorStream(true);
                Process process = pb.start();

                try (BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    while (r.readLine() != null) {}
                }
                
                if (process.waitFor() == 0) {
                    return outputFile;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static CompletableFuture<File> extractThumbnail(File ffmpegExe, File videoFile, File customOutputDir, double second) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!customOutputDir.exists()) {
                    customOutputDir.mkdirs();
                }
                
                String fileName = videoFile.getName();
                String baseName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
                File thumbFile = new File(customOutputDir, baseName + "_thumb.png");

                if (thumbFile.exists()) {
                    return thumbFile;
                }

                ProcessBuilder pb = new ProcessBuilder(
                        ffmpegExe.getAbsolutePath(),
                        "-y",
                        "-ss", String.valueOf(second),
                        "-i", videoFile.getAbsolutePath(),
                        "-vframes", "1",
                        thumbFile.getAbsolutePath()
                );
                
                pb.redirectErrorStream(true);
                Process process = pb.start();

                try (BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    while (r.readLine() != null) {}
                }
                
                if (process.waitFor() == 0) {
                    return thumbFile;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    private static String getCodecForFormat(String format) {
        if (format.equalsIgnoreCase("ogg")) return "libvorbis";
        if (format.equalsIgnoreCase("mp3")) return "libmp3lame";
        return "copy";
    }
}