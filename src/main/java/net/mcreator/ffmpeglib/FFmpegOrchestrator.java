package net.mcreator.ffmpeglib;

import java.io.*;
import java.util.concurrent.CompletableFuture;

public class FFmpegOrchestrator {
    public static CompletableFuture<Void> extractFrames(File ffmpegExe, File videoFile, File targetDir, int fps) {
        return CompletableFuture.runAsync(() -> {
            try {
                if (!targetDir.exists()) targetDir.mkdirs();
                
                ProcessBuilder pb = new ProcessBuilder(
                        ffmpegExe.getAbsolutePath(),
                        "-y",
                        "-i", videoFile.getAbsolutePath(),
                        "-vf", "fps=" + fps,
                        new File(targetDir, "frame_%04d.png").getAbsolutePath()
                );
                pb.redirectErrorStream(true);
                Process process = pb.start();
                
                try (BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    while (r.readLine() != null) {}
                }
                process.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}