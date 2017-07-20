Multithreaded Messenger in Java

Goal: Create a scalable messaging application that allows users to connect and message any other user on the server

How to Run:
- Ensure config.txt is in the current directory with the correct information
- On the server, compile server.java and run the jar file with java -jar Server.jar
- On the client, compile client.java and run the jar file with java -jar Client.jar and follow prompts to message currently active users

Design:

This application is designed to be scalable and reliable by using threading and TCP sockets, while also achieving optimal performance with the use of efficient data structures and design patterns. The server checks a configuration file to determine the number of ports that can be allocated for use and the amount of threads to spawn to handle tasks. The client is configured with the server's IP address to connect to. These configuration files can be used to extend functionality and add other features.

On the server, a hashtable of <String,ClientHandler> will be initialized, where the ClientHandler object is a Runnable that also contains the socket needed to communicate with the client. The server will also have a concurrent, thread safe queue, "ConcurrentLinkedDeque". This queue will contain all messages needed to be sent. The server will spawn Dispatch threads to take messages from the queue and send them to the correct destination, like a thread pool. The use of the hashmap optimizes lookups while the thread safe queue will allow concurrent access to message objects in constant time.

The client will have a simple text-based UI, which the user can interact with to find users and send messages. Only one client may connect to the server per computer. There will be a thread to constantly check for data on the socket and alert any observers when this event occurs. This Observer design pattern makes it simpler to update the "View", informing the user if there is any incoming messages. This simple UI can be expanded into a full-fledged GUI in the future.

Program Flow:

Clients will first connect by sending a UDP message to the server. The server will respond with the TCP port number to connect to. This simple UDP connect protocol will help balance the load (can be amongst multiple servers) and keep a cap on the amount of clients connecting.

The client will then connect to the appropriate TCP port on the server and receive data, such as the current users online.

The client will send a message to a user. On the server side, the ClientHandler thread receives the message and adds it to the dispatch queue, to be appropriately forwarded. The use of a concurrent queue and dispatch thread ensures that the server is scalable, because the ClientHandler thread will go back to listening on the port instead of trying to send the message.

The dispatch thread gets the next message on the queue and its corresponding destination. It looks up the appropriate socket to send to via the hashtable and calls the send() method provided by ClientHandler. It then finds the sender in the HashTable and sends a status message informing whether their message was properly sent or if an error occured.

Once the client gets feedback (ok or error) from the server, it lets the user send a follow up message, or go to home with "/q".

Any messages received will be shown on the console, along with the sender. Once finished, the client can quit with "/q" and a Disconnect message will be sent to the server, so the resources can be deallocated. 

Future Work:

While the messaging application is able to reliably send a message, security is still an issue. Encrypting messages using asymmetric encryption can secure the simple protocol from attackers sniffing on the network.

A more sophisticated GUI on the client end will allow for improved functionality, such as the ability to talk to many users at once.

Adding precise logging on the server end to make application fault tolerant.

Using Natural Language Processing, a chatbot application can be made to talk to the user (in case they have no friends), similar to the help bot on Slack. This can be easily accomplished by adding another client as the chatbot on the server.

Accomplishments:

- Creating a multi-threaded, scalable messaging protocol in Java. 
- Using both UDP sockets for speed and TCP sockets for reliability
- Using optimal data structures for faster lookups and insertions/deletions
- Designing the server to handle many clients and distribute load using threads. Can distribute load across multiple servers
- Making use of Observer Design pattern and follow Model View Controller architecture to design client app

