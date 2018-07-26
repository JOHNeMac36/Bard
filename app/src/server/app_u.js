const http = require('http');
const fs = require('fs');
const formidable = require('formidable');
const express = require('express');
const util = require('util');

var app = express();

app.get('/', function(req, res){ //response to http server GET request at home url
	console.log('GET request at /');
	res.writeHead(200, {'Content-Type': 'text/html'});
	    res.write('<form action="fileupload" method="post" enctype="multipart/form-data">');
	    res.write('<input type="file" name="filetoupload"><br>');
	    res.write('<input type="submit">');
	    res.write('</form>');
	    return res.end();
});

app.post('/fileupload', function(req, res){ //response to http server POST request at home url
	console.log("POST request received at /fileupload");
	res.writeHead(200, {'Content-Type' : 'text/plain'});
	var form = formidable.IncomingForm();
	form.uploadDir = __dirname + "/temp"
	form.keepExtensions = true;
	form.encoding = 'UTF-8';
	
	//inspired by blog post at http://shiya.io/simple-file-upload-with-express-js-and-formidable-in-node-js/

	form.parse(req, function(err, fields, files){
		console.log("Parsing...");
		if (err) {throw err};
	});

	form.on('file', function(name, file){ // the 'file' event is never emitted when receiving an upload from android...
		console.log("Uploaded: " + file.name);
	});

	form.on('end' , function(){
		console.log("Ending...");
		res.json();
		res.end();
	});

	form.on('progress', function(bytesReceived, bytesExpected){
		console.log((bytesReceived/bytesExpected) * 100 + " %");
	})

	form.on('error', function(err){
		console.log("There has been an error.");
		console.log(err);
	})

});

app.listen(80, '0.0.0.0');
console.log("Listening on port 80");