<template>
    <div class="wrapper">
        <div class="player-container">
            <d-player ref="player" @play="play" @pause="pause" :options="options"></d-player>
        </div>
        <div class="input">
            <el-input placeholder="请输入视频链接" v-model="videoPath">
            <el-select v-model="select" slot="prepend" placeholder="请选择">
            <el-option
                v-for="item in selectOptions"
                :label="item.label" 
                :key="item.value"
                :value="item.value"
                ></el-option>
            </el-select>
            <el-button slot="append" icon="el-icon-search" @click="switchVideo"></el-button>
        </el-input>
        </div>
    </div>
</template>

<script>

// let socket;

import {serverIp} from "../../public/config";

export default {
    name:"MyRoom",
    data() {
        return{
            uuid: localStorage.getItem("uuid"),
            select: 1,
            roomId: this.$route.query.roomId,
            id: "",
            player: {},
            socket: {},
            videoPath: "",
            playerStatus: {
                paused: true,
                current: 0,
            },
            selectOptions: [
                {
                    label: "普通视频",
                    value: 1
                },
                {
                    label: "阿里云盘",
                    value: 2
                },
                {
                    label: "B站视频",
                    value: 3
                },
            ],
            options: {
                theme: '#b7daff', // 风格颜色，例如播放条，音量条的颜色
                // loop: false, // 是否自动循环
                lang: 'zh-cn', // 语言，'en', 'zh-cn', 'zh-tw'
                hotkey: true, // 是否支持热键，调节音量，播放，暂停等
                preload: 'auto', // 自动预加载
                mutex: true,
                video: {
                    url: '', // 播放视频的路径
                    defaultQuality: 0, // 默认是HD
                    pic: 'https://cdn-us.imgs.moe/2023/05/29/64740110a8364.jpg', // 视频封面图片
                },
            }
        }
    },
    components: {
    },
    created() {
        setTimeout(() => {
            this.player = this.$refs.player.dp
            this.onload() 
        }, 500)
        setTimeout(() => {
            this.send()
        }, 1000)
        
    },
    methods:{
        onload(){
            if (typeof (WebSocket) == "undefined") {
                console.log("您的浏览器不支持WebSocket");
            } else {
                console.log("您的浏览器支持WebSocket");
                let socketUrl = `ws://${serverIp}:9091/roomServer/` + this.roomId + "/" + this.uuid;
                // if (this.socket != null) {
                //     this.socket.close();
                //     this.socket = null;
                // }
                // 开启一个websocket服务
                this.socket = new WebSocket(socketUrl);
                this.socket.onopen = () => {
                    console.log("websocket已打开");
                };
                this.socket.onmessage = (msg) => {
                    console.log("收到数据====" + msg.data)
                    let data = JSON.parse(msg.data)  // 对收到的json数据进行解析， 类似这样的： {"users": [{"username": "zhang"},{ "username": "admin"}]}
                    if(data.current - this.player.video.currentTime  > 1.0 || data.current - this.player.video.currentTime < -1.0){
                        this.player.video.currentTime = data.current
                    }
                    if(data.paused === true){
                        this.player.pause()
                    } else {
                        this.player.play()
                    }
                    if(data.videoPath !== this.videoPath){
                        this.videoPath = data.videoPath
                        this.switchVideo()
                    }
                };
                //关闭事件
                this.socket.onclose = () => {
                    console.log("websocket已关闭");
                };
                //发生了错误事件
                this.socket.onerror = () => {
                    console.log("websocket发生了错误");
                }
            }
        },
        send(){
            setInterval(()=>{
                this.sendMessage()
            }, 200)
            
        },
        sendMessage(){
            const msg = {}
            msg.current =  this.player.video.currentTime
            msg.paused =  this.player.video.paused
            msg.videoPath = this.videoPath
            
            if (this.socket && this.socket.readyState === 1) {
                this.socket.send(JSON.stringify(msg));
            } else {
                // webscoekt 连接尚未建立
                setTimeout(() => {
                    this.sendMessage(msg);
                }, 1000);
            }
        },
        // 切换视频源事件
		switchVideo() {
            this.player.switchVideo({
                url: this.videoPath
            })
		},
        // // 暂停事件
		// pause() {
        //     // if(this.id !== null || this.id !== ''){
        //     //     clearInterval(this.id)
        //     // }
        //     this.playerStatus.paused = this.player.video.paused
        //     // console.log(this.playerStatus.paused)
		// },
        // // 返回视频当前播放时间
        // currentTime(){
        //     this.playerStatus.current = this.player.video.currentTime
        //     // console.log(this.playerStatus.current)
        // },
        // // 播放事件
        // play(){
        //     this.playerStatus.paused = this.player.video.paused
        //     // console.log(this.playerStatus.paused)
        //     // this.id = setInterval(this.currentTime,1000)
        // },
    },
    destroyed(){
        this.socket.close()
    }
}
</script>

<style scoped>
    
    .el-select .el-input {
        width: 130px;
    }
    @media screen and (min-width: 500px){
        .player-container {
        width: 100%;
        margin: 0 auto;
        margin-top: 25px;
        margin-bottom: 15px;
        }
        .input {
            width: 100%;
            margin: 0 auto;
            
        }
    }
    @media screen and (min-width: 1100px){
        .player-container {
        width: 60%;
        margin: 0 auto;
        margin-top: 25px;
        margin-bottom: 15px;
        }
        .input {
            width: 60%;
            margin: 0 auto;
            
        }
    }
    .wrapper {
        width: 100%;
        min-height: 100vh;
        background-image: linear-gradient(to bottom right, #ffa3e7, #95daff);
        overflow: hidden;
    }
</style>