const http = require('http');
const fs = require('fs');

var loremStream = fs.createReadStream(__dirname + '/lorem.txt', 'utf-8');

const server =  http.createServer(function(req, res){
	console.log("Request received!");
	res.writeHead(200, {'Content-Type': 'text/plain'});
	loremStream.on('data', function(chunk){
		console.log("Chunk sent!");
		res.write(chunk);
	});
	loremStream.on('end', function(){
		console.log("Ending...");
		res.end();
		server.close();
	});
});

server.listen(8080, '0.0.0.0');
console.log("Listening on port 8080");