const http = require('http');
const fs = require('fs');
const formidable = require('formidable');
const express = require('express');
const PassThrough = require('stream').PassThrough;

const streamOptions =  { //turn autoclose off so that data can be added to the stream even when it runs out
  flags: 'r',
  encoding: null,
  fd: null,
  mode: 0o666,
  autoClose: false,
  highWaterMark: 64 * 1024
};

var app =  express();

//hard-coded test
var songList = ['/res/Redbone.mp3', '/res/Devilman No Uta - Devilman Crybaby 2018 Full.mp3', '/res/Dr Seus The Lorax How bad can I be -Official Hd 1080p-.mp3'];
var i = 0;

app.get('/song', function(req, res){  //response to http server GET request at 'song' url
	res.writeHead(200, {'Content-Type': 'text/plain'});

	//metadata
	res.write("Song name goes here.");
	console.log("GET request received at /song");
	console.log(songList[i]);

	var songStream = fs.createReadStream(__dirname + songList[i], streamOptions);
	songStream.pipe(res);
	i++;

	if (i > 3) {
		res.end();
	}

});


app.listen(6666, '::');
console.log("Listening on port 6666");