# import packages
from imutils.video import VideoStream
from collections import deque 
from imutils.video import FPS
import numpy as np
import argparse
import imutils
import time
import cv2

#define arguments
ap = argparse.ArgumentParser()
ap.add_argument("-b", "--buffer", type=int, default=64,
	help="max buffer size")
args = vars(ap.parse_args())

#initialize global variables
cLeft = 0
cRight = 0
cUp = 0
cDown = 0

#define range of RGB colors to be segmented by filter
#tuple is defined as blue,green,red ------>
color_lowerBound = (30, 0, 0)
color_upperBound = (255, 200, 200)

#initialize list of points for tracking 
pos = deque(maxlen=args["buffer"])

#initialze real time video
video = VideoStream(src=0).start()
time.sleep(2.0) #wait for camera
fps = FPS().start() #provides status information

while True:
	frame = video.read()
	frame = imutils.resize(frame,width=800) #resize video frame for faster processing

	hsv = cv2.cvtColor(frame,cv2.COLOR_BGR2HSV) #gaussian blur on frame to construct mask

	mask = cv2.inRange(hsv,color_lowerBound,color_upperBound) #construct mask

	#dialate and erode to remove any outliers
	mask = cv2.erode(mask, None, iterations=5)
	mask = cv2.dilate(mask, None, iterations= 5)

	#find countours of the newly created mask
	contours = cv2.findContours(mask.copy(), cv2.RETR_EXTERNAL,
		cv2.CHAIN_APPROX_SIMPLE) [-2]

	center = None #initialize center of subject

	#find the largest countour
	#use it to enclose subject in the minimum enclosing circle
	#find center of subject
	if len(contours) > 0:
		c = max(contours, key=cv2.contourArea)
		((x, y), radius) = cv2.minEnclosingCircle(c)
		M = cv2.moments(c)
		center = (int(M["m10"] / M["m00"]), int(M["m01"] / M["m00"]))


	pos.appendleft(center) #update center of subject

	#construct line to track center of subject
	for i in xrange(1,len(pos)): 

		if pos[i-1] is None or pos[i] is None: #handle edge case error during initialization
			continue

		line_width = int(np.sqrt(args["buffer"] / float(i + 1)) * 2.5)
		cv2.line(frame,pos[i - 1], pos[i], (0,0,255), line_width)


		#construct logic based on relative position of subject to center of the frame
		cv2.circle(frame,(400,200), 10, (0,255,0), -1) #draw circle at the center of frame

		cv2.line(frame,(400,200),pos[i],(0,255,0),line_width/2) #draw line from subject to frame center

		distance = np.subtract((400,200), pos[i]) #compute distance between subject and frame center

		#compute logic based on distance 

		if distance[0] < -50:
			cRight += 1

			if cRight > 500:
				print("right")
				xRight = 0


		if distance[0] > 50:
			cLeft += 1

			if cLeft > 500:
				print("left")
				cLeft = 0

		if distance[1] > 50:
			cUp += 1

			if cUp > 500:
				print ("up")
				cUp = 0

		if distance[1] < -50:
			cDown += 1

			if cDown > 500:
				print("down")
				cDown = 0


	cv2.imshow("Mask", mask)
	cv2.imshow("Frame", frame)

	key = cv2.waitKey(1) & 0xFF

	#exit program if q key is pressed
	if key == ord("q"):
		break

	fps.update() #display status information

fps.stop()
print("[INFO] elapsed time: {:.2f}".format(fps.elapsed()))
print("[INFO] approx. FPS: {:.2f}".format(fps.fps()))

cv2.destroyAllWindows()
vs.stop()





