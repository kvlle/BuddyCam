import socket
TCP_IP='35.231.35.232'
TCP_PORT=31235
BUFFER_SIZE = 32
while True:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((TCP_IP, TCP_PORT))
    data = s.recv(BUFFER_SIZE)
    print('Received data', data)
