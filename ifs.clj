(ns fractals.ifs
    (:require [tech.v3.tensor :as dtt]
              [tech.v3.datatype.functional :as dfn]
              [tech.v3.libs.buffered-image :as bufimg]
              [fastmath.matrix :refer (mat2x2 mulv)]
              [fastmath.vector :refer (vec2 add mult)])
    (:import [javax.imageio ImageIO]
             [java.io File]))


(defn weighted-rand-index
  [probs]
  (let [r (rand)
        cum (reductions + probs)]
    (loop [i 0]
      (if (>= (nth cum i) r)
        i
        (recur (inc i))))))

(def barnsley-mats
  [(mat2x2 0.0   0.0
           0.0   0.16)
   (mat2x2 0.85  0.04
          -0.04  0.85)
   (mat2x2 0.20 -0.26
           0.23  0.22)
   (mat2x2 -0.15 0.28
            0.26 0.24)])

(def barnsley-trans
  [(vec2 0.0 0.0)
   (vec2 0.0 1.6)
   (vec2 0.0 1.6)
   (vec2 0.0 0.44)])

(def barnsley-probs
  [0.01 0.85 0.07 0.07])

(defn function-system [matrices vectors probs]
  (fn [p]
      (let [i (weighted-rand-index probs)
            m (nth matrices i)
            v (nth vectors i)]
        (add (mulv m p) v))))

(def iterated-function-system (iterate (function-system barnsley-mats barnsley-trans barnsley-probs) (vec2 0.0 0.0)))

(def w 2560)
(def h 1440)
(def t (dtt/new-tensor [h w] :datatype :uint8))

(doseq [p (take 10000000 iterated-function-system)]
       (let [[y x] (add (mult p 250) (vec2 650 10))]
         (dtt/mset! t y x 255)))

(def image (bufimg/tensor->image (dfn/- 255 t)))

(ImageIO/write image "png" (File. "ifs.png"))
