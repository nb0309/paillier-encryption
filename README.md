# paillier-encryption
Paillier Encryption System - Server and Client
Introduction
This project implements a basic Paillier Encryption System, demonstrating the use of the Paillier cryptosystem for secure computation between a server and a client. The server performs computations on encrypted data received from the client and sends the results back.

Prerequisites
Python 3.x
phe library 
bash
```pip install phe```

How to Use
Run the Server Script:

Open a terminal and navigate to the directory containing the server script (server.py). Run the following command:

bash
```
python server.py
```
The server will start listening for incoming connections.

Run the Client Script:

Open a new terminal window and navigate to the directory containing the client script (client.py). Run the following command:

bash
```
python client.py
```
A Tkinter GUI window will appear, allowing you to input a number, select an operation (addition or multiplication), and encrypt and send the data to the server.

Enter Constants:

If the chosen operation requires a constant, a dialog box will prompt you to enter the constant.

View Results:

The client will receive the result of the computation from the server, decrypt it, and display the decrypted result.

Important Files
server.py: The server-side script that listens for client connections, performs computations on encrypted data, and sends the results back.
client.py: The client-side script that provides a Tkinter GUI for user interaction, encrypts and sends data to the server, and displays the decrypted results.
Notes
The Paillier encryption is implemented using the phe library.
The communication between the server and the client is established through sockets, and data serialization is done using the pickle module.
Author
Navabhaarathi
