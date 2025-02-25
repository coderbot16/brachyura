package io.github.coolcrabs.brachyura.minecraft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.coolcrabs.brachyura.dependency.Dependency;
import io.github.coolcrabs.brachyura.dependency.JavaJarDependency;
import io.github.coolcrabs.brachyura.dependency.NativesJarDependency;
import io.github.coolcrabs.brachyura.mappings.Namespaces;
import io.github.coolcrabs.brachyura.minecraft.LauncherMeta.Version;
import net.fabricmc.mappingio.tree.MappingTree;

class MinecraftTest {
    @Test
    void mcTest1_6_4() {
        VersionMeta meta = Minecraft.getVersion("1.6.4");
        assertNotNull(meta);
        Path client = Minecraft.getDownload("1.6.4", meta, "client");
        assertTrue(Files.isRegularFile(client));
        for (Dependency lib : Minecraft.getDependencies(meta)) {
            if (lib instanceof JavaJarDependency) {
                assertNotNull(((JavaJarDependency)lib).jar);
            } else if (lib instanceof NativesJarDependency) {
                assertNotNull(((NativesJarDependency)lib).jar);
            }
        }
    }

    @Test
    void mcTest1_17() {
        VersionMeta meta = Minecraft.getVersion("1.17");
        assertNotNull(meta);
        Path client = Minecraft.getDownload("1.17", meta, "server");
        assertTrue(Files.isRegularFile(client));
        for (Dependency lib : Minecraft.getDependencies(meta)) {
            if (lib instanceof JavaJarDependency) {
                assertNotNull(((JavaJarDependency)lib).jar);
            } else if (lib instanceof NativesJarDependency) {
                assertNotNull(((NativesJarDependency)lib).jar);
            }
        }
    }

    @Test
    void ojmap1_16() {
        MappingTree mappings = Minecraft.getMojmap("1.16.5", Minecraft.getVersion("1.16.5"));
        System.out.println(mappings.getNamespaceId(Namespaces.OBF));
        assertEquals(-1, mappings.getNamespaceId(Namespaces.OBF));
        assertEquals(0, mappings.getNamespaceId(Namespaces.NAMED));
        assertEquals("youJustLostTheGame", mappings.getClass("ddt").getMethod("a", "()V").getName(0));
    }

    @Disabled("Very agresssive")
    @Test
    void downloadAllLibs() {
        LauncherMeta lmeta = LauncherMetaDownloader.getLauncherMeta();
        for (Version versionMeta : lmeta.versions) {
            if (versionMeta.type.equals("snapshot")) continue;
            VersionMeta meta = Minecraft.getVersion(versionMeta.id);
            for (Dependency lib : Minecraft.getDependencies(meta)) {
                if (lib instanceof JavaJarDependency) {
                    assertNotNull(((JavaJarDependency)lib).jar);
                } else if (lib instanceof NativesJarDependency) {
                    assertNotNull(((NativesJarDependency)lib).jar);
                }
            }
        }
    }
}
