echo;
echo '添加变更'
git add .
echo -n "请输入本次提交说明:"
read  message
git commit -m $message
echo '更新远程代码'
git pull
echo '提交代码到远程'
git push
echo '提交完毕'
echo;
