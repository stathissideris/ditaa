(ns ditaa.visual-tests
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io])
  (:import [org.stathissideris.ascii2image.test VisualTester]))

(defn generate-comparison-report []
  (VisualTester/generate))

(defn generate-expected-images []
  (VisualTester/generateImages
   (VisualTester/getFilesToRender) "test-resources/images-expected"))

(deftest generated-images-pixel-equal-to-expected
  (VisualTester/generateImages
   (VisualTester/getFilesToRender) "test-resources/images-actual")

  (doseq [[file idx] (map vector (VisualTester/getFilesToRender) (range))]
    (testing (str "file: " file)
      (is (true? (VisualTester/imagesAreEqual (io/file file) idx)))))

  ;;TODO compare SVGs
  )
