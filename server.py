from phe import paillier
import socket
import pickle


server_host = "127.0.0.1"
server_port = 12345


public_key, private_key = paillier.generate_paillier_keypair()


server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.bind((server_host, server_port))
server_socket.listen()

print(f"Server is listening on {server_host}:{server_port}")

def compute_on_encrypted_data(encrypted_data, constant, operation):
    if operation == "add":
        result = encrypted_data + constant
    elif operation == "multiply":
        result = encrypted_data * constant
    else:
        raise ValueError("Invalid operation")
    return result

while True:
    # Wait for a connection from the client
    client_socket, client_address = server_socket.accept()
    print(f"Accepted connection from {client_address}")

    # Receive encrypted message and operation from the client
    data = client_socket.recv(4096)
    encrypted_message, operation, constant = pickle.loads(data)

    # Perform computation on the encrypted message
    result = compute_on_encrypted_data(encrypted_message, constant, operation)

    # Send the result back to the client
    result_bytes = pickle.dumps(result)
    client_socket.send(result_bytes)

    # Close the connection with the client
    client_socket.close()
    