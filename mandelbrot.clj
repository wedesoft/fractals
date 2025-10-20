(ns fractals.mandelbrot
    (:require [tech.v3.tensor :as dtt]
              [tech.v3.libs.buffered-image :as bufimg]
              [complex.core :as c])
    (:import [javax.imageio ImageIO]
             [java.io File]))

(defn sqr [x] (* x x))
(defn csqr [x]  (+ (sqr (c/real-part x)) (sqr (c/imaginary-part x))))

(def w 2560)
(def h 1440)

(def t (dtt/compute-tensor
         [h w]
         (fn [y x]
             (let [c (c/complex (+ -2.5 (* x (/ 4.0 w)))
                                (+ -1.2 (* y (/ 2.4 h))))]
               (loop [z (c/complex 0 0)
                      i 0]
                     (if (or (>= i 200) (> (csqr z) 4))
                       (- 255 (quot (* i 255) 200))
                       (recur (c/+ (c/* z z) c)
                              (inc i))))))
         :uint8))

(def image (bufimg/tensor->image t))

(ImageIO/write image "png" (File. "mandelbrot.png"))
