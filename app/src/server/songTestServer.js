const http = require('http');
const fs = require('fs');

var songStream = fs.createReadStream(__dirname + '/Deon Custom - Aloha.mp3');

const server =  http.createServer(function(req, res){
	console.log("Request received!");
	res.writeHead(200, {'Content-Type': 'text/plain'});

	songStream.on('data', function(chunk){
		console.log("Chunk sent!");
		res.write(chunk);
	});
	songStream.on('end', function(){
		console.log("Ending...");
		res.end();
		server.close();
	});
});

server.listen(8080, '::');
console.log("Listening on port 8080");