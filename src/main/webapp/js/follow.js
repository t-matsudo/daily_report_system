/**
 *
 */
const fb = document.getElementById('follow');
const ufb = document.getElementById('unfollow');
const id = document.getElementById('employeeId');
const code = document.getElementById('employeeCode');
const fs = document.getElementById('followState');

//フォローボタンの初期設定
if(Number(fs.innerText)){
    ufb.classList.remove("hidden");
    fb.classList.add("hidden");
}else{
    fb.classList.remove("hidden");
    ufb.classList.add("hidden");
}

const show_var = (myConst) => {
  console.log("JSで受けっとった値: " + myConst);
};

fb.addEventListener('click', () =>{
    console.log("クリック動作");
    (async ()=> {
    //const test = await new Follow().test();
    //location.href = "?action=Employee&command=index";
    const connect = await new HttpConnection(`?action=Follow&command=follow&id=${id.innerText}`).toGet();
    ufb.classList.remove("hidden");
    fb.classList.add("hidden");
    })();
});

ufb.addEventListener('click', (e) =>{
    console.log("クリック動作");
    (async () => {
    //const test = await new Follow().test();
    //location.href = "?action=Employee&command=index";
    const connect = await new HttpConnection(`?action=Follow&command=unfollow&id=${id.innerText}`).toGet();
    fb.classList.remove("hidden");
    ufb.classList.add("hidden");
    })();
});

class HttpConnection{
    constructor(url){
        this.url = url;
    }

    async toGet(){
        return await this.getConnect();
    }

    getConnect(){
        const httpTest = new XMLHttpRequest();
        httpTest.open('GET', this.url);
        httpTest.send();

        httpTest.onreadystatechange = function(){
            if(httpTest.readyState === 4 && httpTest.status === 200){
                console.log("正常に接続されました。");
            }else{
                console.log(httpTest.status);
                console.log(httpTest.readyState);
                console.log("接続に問題がありました。");
            }
        };
    }
}

class Follow{
    async test(){
        return await this.asyncReturnSomething();
    }

    asyncReturnSomething(){
        return new Promise((resolve) =>{
            setTimeout(()=>{
                resolve("これはtestです");
            }, 1000);
        });
    }
}

/*const mysql = require('mysql');
 const connection = mysql.createConnection({
    host : 'localhost',
    user : 'repuser',
    password : 'reppass',
    database : 'daily_report_system'
});

 console.log(`${connection.host}、${connection.user}、${connection.password}、${connection.database}`);

connection.connect((err) =>{
     console.log("接続の動作しました");
    if(err){
        console.error(`Error!: ${err.stack}`);
        return;
    }

    console.log(`Success! Connected as id${connection.threadId}`);
});

connection.end();
*/