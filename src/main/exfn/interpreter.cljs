(ns exfn.interpreter)

(defn inc-dec-mod [f n]
  (mod (f n) 256))

(defn reducer [{:keys [instr open] :as state} i]
  (cond
    (= i \[) (-> state
                 (update :open conj instr)
                 (update :instr inc))
    (= i \]) (-> state
                 (update :open pop)
                 (update :instr inc)
                 (update :res assoc instr (peek open))
                 (update :res assoc (peek open) instr))
    :else    (update state :instr inc)))

(defn build-jmp-table [code]
  (prn "here!")
  (->> code
       (reduce reducer {:instr 0 :open [] :res {}})
       :res))

(defn get-jmp-target [{:keys [dp memory ip jmp-table]} direction]
  (let [byte-at-dp (memory dp 0)]
    (cond
      (or (and (= direction :forward) (zero? byte-at-dp))
          (and (= direction :backward) (not (zero? byte-at-dp))))
      (inc (jmp-table ip))

      (or (and (= direction :forward) (not (zero? byte-at-dp)))
          (and (= direction :backward) (zero? byte-at-dp)))
      (inc ip))))

(defn read-input [input]
  (if (empty? input)
    0
    (.charCodeAt (first input))))

(defn brain-fuck [code input]
  (let [code (seq code)
        code-len (count code)]
    (loop [vm {:ip        0
               :input     (seq input)
               :jmp-table (build-jmp-table code)
               :dp        0
               :memory    {}}]
      (if (= (:ip vm) code-len)
        (apply str (vm :output))
        (let [cur (nth code (:ip vm))]
          (condp = cur
            \+ (recur (-> vm
                          (update :ip inc)
                          (update-in [:memory (vm :dp)] (fnil (partial inc-dec-mod inc) 0))))

            \- (recur (-> vm
                          (update :ip inc)
                          (update-in [:memory (vm :dp)] (fnil (partial inc-dec-mod dec) 0))))

            \> (recur (-> vm
                          (update :ip inc)
                          (update :dp inc)))

            \< (recur (-> vm
                          (update :ip inc)
                          (update :dp dec)))

            \. (recur (-> vm
                          (update :ip inc)
                          (update :output (fnil conj []) (.fromCharCode js/String (get-in vm [:memory (vm :dp)] 0)))))

            \, (recur (-> vm
                          (update :ip inc)
                          (assoc-in [:memory (vm :dp)] (read-input (vm :input)))
                          (update :input rest)))

            \[ (recur (let [new-ip (get-jmp-target vm :forward)]
                        (assoc-in vm [:ip] new-ip)))

            \] (recur (let [new-ip (get-jmp-target vm :backward)]
                        (assoc-in vm [:ip] new-ip)))

            (recur (-> vm (update :ip inc)))))))))

(def hello-world "++++++++[>++++[>++>+++>+++>+<<<<-]>+>+>->>+[<]<-]>>.>---.+++++++..+++.>>.<-.<.+++.------.--------.>>+.>++.")
(def fib "+++++++++++>+>>>>++++++++++++++++++++++++++++++++++++++++++++>++++++++++++++++++++++++++++++++<<<<<<[>[>>>>>>+>+<<<<<<<-]>>>>>>>[<<<<<<<+>>>>>>>-]<[>++++++++++[-<-[>>+>+<<<-]>>>[<<<+>>>-]+<[>[-]<[-]]>[<<[>>>+<<<-]>>[-]]<<]>>>[>>+>+<<<-]>>>[<<<+>>>-]+<[>[-]<[-]]>[<<+>>[-]]<<<<<<<]>>>>>[++++++++++++++++++++++++++++++++++++++++++++++++.[-]]++++++++++<[->-<]>++++++++++++++++++++++++++++++++++++++++++++++++.[-]<<<<<<<<<<<<[>>>+>+<<<<-]>>>>[<<<<+>>>>-]<-[>>.>.<<<[-]]<<[>>+>+<<<-]>>>[<<<+>>>-]<<[<+>-]>[<+>-]<<<-]")
(def sorter ">>,[>>,]<<[[<<]>>>>[<<[>+<<+>-]>>[>+<<<<[->]>[<]>>-]<<<[[-]>>[>+<-]>>[<<<+>>>-]]>>[[<+>-]>>]<]<<[>>+<<-]<<]>>>>[.>>]")
(def bf-generator "+++++[>+++++++++<-],[[>--.++>+<<-]>+.->[<.>-]<<,]")
(def reverse-input ">,[>,]<[.<]")

(comment
  (brain-fuck sorter "JFIDOSFINF")
  (brain-fuck hello-world "")
  (brain-fuck reverse-input "ABCDEF")
  (brain-fuck fib "")
  )