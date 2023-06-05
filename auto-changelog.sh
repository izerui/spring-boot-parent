# npm install -g conventional-changelog-cli
# 例如： feat: xxxx
#   标识名       	说明	                                        是否会出现在 CHANGELOG 中
#   feat        	新功能（feature）                             会
#   fix       	  修补bug	                                    会
#   docs        	文档（documentation）                         自行决定
#   style       	格式（不影响代码运行的变动）                	     自行决定
#   refactor      重构（即不是新增功能，也不是修改bug的代码变动）      自行决定
#   test        	增加测试	                                     自行决定
#   chore       	构建过程或辅助工具的变动	                       自行决定
conventional-changelog -p angular -i CHANGELOG.md -s -r 0