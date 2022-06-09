echo;
echo '====================== 开始提交变更 ======================'
echo '开始添加变更：git add .'
git add .
echo;

echo -n "提交注释:"
read  message

git commit -m $message
echo;

echo '更新远程代码：git pull'
git pull
echo;

echo '提交代码到远程：git push'
git push
echo;

echo `====================== 提交完毕 ======================`
echo;
