# druid

[![Build Status](https://travis-ci.org/alibaba/druid.svg?branch=master)](https://travis-ci.org/alibaba/druid)
[![Coverage Status](https://img.shields.io/codecov/c/github/alibaba/druid/master.svg)](https://codecov.io/github/alibaba/druid?branch=master&view=all#sort=coverage&dir=asc)  
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.alibaba/druid/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.alibaba/druid/)
[![GitHub release](https://img.shields.io/github/release/alibaba/druid.svg)](https://github.com/alibaba/druid/releases)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

Introduction
---

- git clone https://github.com/alibaba/druid.git
- cd druid && mvn install
- have fun.

Documentation
---

- 中文 https://github.com/alibaba/druid/wiki/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98
- English https://github.com/alibaba/druid/wiki/FAQ

Tips:
- Tips for deploying on WindowsUnder Windows the following error may occur: gpg: cannot open tty `no tty': No such file or directory.
  This can be fixed by configuring gpg through an active profile in .m2\settings.xml where also the Sonatype password is stored:
	<settings>
	  <servers>
		<server>
		  <id>sonatype-nexus-staging</id>
		  <username>[username]</username>
		  <password>[password]</password>
		</server>
	  </servers>
	  <profiles>
		<profile>
		  <id>gpg</id>
		  <properties>
			<gpg.executable>gpg</gpg.executable>
			<gpg.passphrase>[password]</gpg.passphrase>
		  </properties>
		</profile>
	  </profiles>
	  <activeProfiles>
		<activeProfile>gpg</activeProfile>
	  </activeProfiles>
	</settings>
