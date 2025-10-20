(ns fractals.julia
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
             (loop [x (c/complex (+ -1.5 (* x (/ 3.0 w)))
                                 (+ -0.9 (* y (/ 1.8 h))))
                    i 0]
                   (if (or (>= i 200) (> (csqr x) 4))
                     (- 255 (quot (* i 255) 200))
                     (recur (c/+ (c/* x x) (c/complex -0.79 0.15))
                            (inc i)))))
         :uint8))

(def image (bufimg/tensor->image t))

(ImageIO/write image "png" (File. "julia.png"))
