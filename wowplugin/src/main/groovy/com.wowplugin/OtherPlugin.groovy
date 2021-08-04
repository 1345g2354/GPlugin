package com.wowplugin

import org.gradle.api.Plugin
import org.gradle.api.Project
public class OtherPlugin implements Plugin<Project>{
    @Override
    void apply(Project project) {
        // 实现一个名称为testPlugin的task，设置分组为 myPlugin，并设置描述信息
        project.task('testOtherPlugin', group: "OtherPlugin", description: "This is my test plugin")  {
            println "## This is my second gradle plugin in testPlugin task"
        }
        println "** This is my second gradle plugin"
    }
}