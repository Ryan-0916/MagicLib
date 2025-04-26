package com.magicrealms.magiclib.bukkit.manage.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author Ryan-0916
 * @Desc 该类用于存储和管理文件的镜像信息，包括：
 * 1. 文件的镜像目录路径（镜像文件的位置）
 * 2. 源文件目录路径（原始文件的位置）
 * 3. 文件的 YamlConfiguration 对象（用于存储和读取文件配置信息）
 * 主要用途：
 *  - 用于记录文件在镜像操作中的路径和配置。
 *  - 方便对文件的镜像管理和配置操作。
 * 成员变量：
 * - mirrorPath: String 镜像文件的路径
 * - sourcePath: String 源文件的路径
 * - yamlConfiguration: YamlConfiguration 文件的 YAML 配置信息
 * @date 2024-12-18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileMirrorInfo {
    private String mirrorPath;
    private String sourcePath;
    private YamlConfiguration yamlConfiguration;
}
