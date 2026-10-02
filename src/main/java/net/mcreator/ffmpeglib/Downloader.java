package net.mcreator.ffmpeglib;

import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class Downloader {
    private static final String DOWNLOAD_URL = "https://github.com/BtbN/FFmpeg-Builds/releases/download/latest/ffmpeg-master-latest-win64-gpl.zip";

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        setupFfmpeg();
    }

    private static void setupFfmpeg() {
        File configDir = FMLPaths.GAMEDIR.get().toFile();
        File libDir = new File(configDir, "config/ffmpeglib");
        File ffmpegExe = new File(libDir, "ffmpeg.exe");

        if (ffmpegExe.exists()) return;

        if (!libDir.exists()) libDir.mkdirs();

        File zipFile = new File(libDir, "ffmpeg.zip");

        try {
            downloadFile(DOWNLOAD_URL, zipFile);
            unzipAndExtractFfmpeg(zipFile, libDir);
            zipFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void downloadFile(String url, File destination) throws IOException {
        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void unzipAndExtractFfmpeg(File zipFile, File outputDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith("bin/ffmpeg.exe")) {
                    File targetFile = new File(outputDir, "ffmpeg.exe");
                    try (OutputStream os = new FileOutputStream(targetFile)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            os.write(buffer, 0, len);
                        }
                    }
                    break;
                }
                zis.closeEntry();
            }
        }
    }
}