echo;
echo '====================== 开始提交变更 ======================'
echo `当前目录是：pwd`
echo;

echo 开始添加变更：git add .
git add .
echo;

set /p declation=输入提交的commit信息:
git commit -m "%declation%"
echo;

echo 更新远程代码：git pull
git pull
echo;

echo 将变更情况提交到远程分支：git push
git push
echo;

echo '====================== 提交完毕 ======================'
echo;
