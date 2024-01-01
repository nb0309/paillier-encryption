from tkinter import Tk, Label, Entry, Button, StringVar, OptionMenu, messagebox
from tkinter import simpledialog  # Add this line
from phe import paillier
import socket
import pickle


class PaillierClientGUI:
    def __init__(self, master):
        self.master = master
        master.title("Paillier Encryption Client")
        master.geometry("400x300")

        
        self.server_host = "127.0.0.1"
        self.server_port = 12345

        
        self.public_key, self.private_key = paillier.generate_paillier_keypair()

        
        self.label = Label(master, text="Enter a number:", font=("Helvetica", 14))
        self.label.pack(pady=10)

        self.entry = Entry(master, font=("Helvetica", 12))
        self.entry.pack(pady=10)

        self.operation_label = Label(master, text="Select Operation:", font=("Helvetica", 14))
        self.operation_label.pack(pady=5)

        
        self.operation_var = StringVar(master)
        self.operation_var.set("add")  
        operations = ["add", "multiply"]
        self.operation_menu = OptionMenu(master, self.operation_var, *operations)
        self.operation_menu.pack(pady=5)

        self.encrypt_button = Button(master, text="Encrypt and Send", command=self.encrypt_and_send, font=("Helvetica", 12), bg="#4CAF50", fg="white")
        self.encrypt_button.pack(pady=10)

        self.result_label = Label(master, text="", font=("Helvetica", 14))
        self.result_label.pack(pady=10)

    def encrypt_and_send(self):
        try:
            
            plaintext_message = int(self.entry.get())
            operation = self.operation_var.get()
            constant = int(self.get_constant_for_operation(operation))

            
            encrypted_message = self.public_key.encrypt(plaintext_message)

            
            data = (encrypted_message, operation, constant)
            data_bytes = pickle.dumps(data)
            with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as client_socket:
                client_socket.connect((self.server_host, self.server_port))
                client_socket.send(data_bytes)

                
                result_bytes = client_socket.recv(4096)
                result = pickle.loads(result_bytes)

                
                decrypted_result = self.private_key.decrypt(result)

                
                result_text = f"Decrypted Result: {decrypted_result}"
                self.result_label.config(text=result_text)

        except ValueError:
            messagebox.showerror("Error", "Please enter a valid number for the constant.")


    def get_constant_for_operation(self, operation):
        
        constant_str = simpledialog.askstring("Constant", f"Enter the constant for {operation}:")
        return int(constant_str) if constant_str is not None else 0


if __name__ == "__main__":
    root = Tk()
    gui = PaillierClientGUI(root)
    root.mainloop()
