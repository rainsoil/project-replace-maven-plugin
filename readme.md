## maven项目包替换

实现需求:
1. 包路径全局替换
2. 变量替换



## 使用
```xml


	<plugin>
				<groupId>com.su60.project.replace</groupId>
				<artifactId>project-replace-maven-plugin</artifactId>
				<version>1.0</version>
				<configuration>
					<!-- 是否替换目录-->
					<replacePath>true</replacePath>
					<sourcedir>${project.basedir}</sourcedir>
					<targetdir>${project.basedir}</targetdir>
					<!-- 是否删除老文件-->
					<deleteOld>true</deleteOld>
					<!--替换的文件正则 -->
					<includes>
						<include>**/*.java</include>
						<include>**/*.xml</include>
					</includes>

					<excludes>
						<exclude>**/.git/**</exclude>
						<exclude>**/.idea/**</exclude>
						<exclude>**/target/**</exclude>
						<exclude>**/doc/**</exclude>

					</excludes>


					<!-- 替换配置-->
					<replacements>
						<replacement>
							<source>com.xxx</source>
							<target>com.xxx</target>
						</replacement>
					
					</replacements>

				</configuration>
			</plugin>


```