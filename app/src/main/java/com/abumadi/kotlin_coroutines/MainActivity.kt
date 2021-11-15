package com.abumadi.kotlin_coroutines

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

private const val TAG = "Main Activity "

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {
    lateinit var viewModel: TimerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //what is runBlocking?:make block until inside {body} finished >>working in main thread.
//        Log.d(TAG, "main thread")
//        runBlocking {//will run our builder coroutine w/o scope.start(thread){body}
//            printMyTextAfterDelayCoroutine("Abdullah")
//        }
//        Log.d(TAG, "back to main thread ")
///////////////////////////////////////////////////////////////////////////////////////////
        //*parallel and not parallel>>
//        GlobalScope.launch {
//            printMyTextAfterDelayCoroutine("Abdullah1")
//            printMyTextAfterDelayCoroutine("Abdullah2")
//        }
        ////////////////////////////////////////////////////////////////////////////

//         var networkData:String?=null
//         var dataBaseData:String?=null

//        GlobalScope.launch {
//            launch {networkData = getNetworkData() }
//            launch {dataBaseData = getDataBaseData()}
//
//            if(networkData == dataBaseData){
//                Log.d(TAG, "equals")
//            }else Log.d(TAG, "not equals")
//        }
//    }//in this codes will not wait until data come from get functions >>null=null>> true
        // in this case we need to make this (if)wait 2.5 sec until data takes variables
        //coroutine solved this problem by Async/Await

//        GlobalScope.launch {
//
//        val time = measureTimeMillis {
//            val networkData = async { getNetworkData() }//async for suspend fun
//            val dataBaseData = async { getDataBaseData() }//will return Deferred value {String}
//
//            if (networkData.await() == dataBaseData.await()) {//networkData will be String after Deferred finished
//                Log.d(TAG, "equals")
//            } else Log.d(TAG, "not equals")
//        }
//        Log.d(TAG, "time=$time")
//        }//Deferred wait value and come with it when it is ready


////////////////////////////////////////////////////////////////////////
        //Jobs
        val parentJop = Job()//will be parent of parents
        val coroutineScope: CoroutineScope =
            CoroutineScope(Dispatchers.IO + parentJop)//use it instead of GlobalScope

//        coroutineScope.launch {  }//customize with IO Dispatcher+parent of it is parent jop>>in onStop()>>coroutineScope.canceled

        val jop: Job =
            GlobalScope.launch(parentJop) {//launch(parentJop)>>this parent will be child of parentJob
                val child1 = launch { getNetworkData() }
                val child2 = launch { getDataBaseData() }
                Log.d(TAG, "waiting..")
                joinAll(
                    child1,
                    child2
                )//or child.join()>>third launch will wait child1 and child2 finished cuz of join
                launch {
//                    delay(2000)
                    Log.d(TAG, "OK")
                }
            }
    }
}
//        jop.cancel()//will cancel the parent >>childes will be canceled
//        parentJop.cancel()//we use it to cancel all coroutines parents after onStop().

        ////////////////////////////////////////////////////////////////////////////////////////
        //Channels :if I have stream of values can not use Differed cuz it return 1 value

        //note: capacity of basket called (buffer)>>basket put data from 1 and 2 will take it when be ready
//        val kotlinChannel= Channel<String>()//note:rendezvous is capacity of basket will pass data from first and arrived in second by default is zero
//
//        val charList= arrayOf("A","B","C","D")
//        runBlocking {
//            //provider
//            launch {
//                for(char in charList){
////                    kotlinChannel.send(char)
//                    kotlinChannel.offer(char)//do not suspend >>using it for ex:user click 7 times and we need just the first click>>remove launch from the second coroutine and will take just A
////                    delay(1000)//wait 1 sec after every send
//                }
//            }
//            //collector
//
//                for(char in kotlinChannel){
////                    delay(2000)
//                    Log.d(TAG, "here: $char")
//                }

//        }

////////////////////////////////////////////////////
//use flow

//        runBlocking {
//            flow<Int> {
//                for (i in 1..10) {
//                    emit(i)//like send in channel
//                    delay(2000)
//                    Log.d(TAG, "here producer $i")//will not work until collector connected
//                }
//            }//producer
//                .filter { i -> i < 5 }//intermediate
//
//                .buffer()//to make producer and collector parallel (everyone in coroutine )
//                .collect {
//                    delay(2000)
//                    Log.d(TAG, "here collector $it")//like observer
//                }//collector
//        }//cuz collect type in suspend >>have to be inside coroutine body

//    //combine more than one flow>>I want it to display at same time
//        runBlocking {
//            val flow1= flow<Int> {
//                for (i in 1..3) {
//                    emit(i)
//                    delay(2000)
//                }
//            }
//            val flow2= flow<String> {
//                val list : List<String> = listOf("A","B","C","D")
//                for (i in list) {
//                    emit(i)
//                    delay(1000)
//                }
//            }
//            flow1.zip(flow2){a,b->"$a:$b"}//to combine flow1 and flow2 in one string,in time take biggest:ex:delay 1 sec and 2 sec >>take 2 ,in data take smaller:ex:A B C D &1,2,3 >>D will be ignored
//                    .collect {
//                        Log.d(TAG, it)
//                    }
//            }//when we make combination >>it will be together always


///////////////////////////////////////////////////
//        viewModel = ViewModelProvider(this).get(TimerViewModel::class.java)
//        viewModel.startTimer()
////        viewModel.timerLiveData.observe(this, {
////            MyText.text = it.toString()
////            Log.d(TAG, it.toString())
////        })
//        lifecycleScope.launch{//launchWhenStarted>>to stop code out of activity visible>>like liveData
//            viewModel.timerStateFlow.collect {
//                MyText.text = it.toString()
//                Log.d(TAG, it.toString())
//            }
//        }
//    }


    /**
     * Coroutine Notes:
     * 1)note:Continuation is the parameter give me power to stop and resume my process
     * (it is hiding parameter in coroutines function "suspend fun" you can find it after make java decompiler)

     * 2)suspend fun has 3 Properties:
     *give me ability to (stop and resume function)
     *NOT responsible to go to background thread
     *should call from another suspend fun or coroutine builder

     * 3)Coroutine builder : Scope.Starting[launch or async].(Thread (Dispatchers)){Body}//four parts.

     * 4)there is 4 threads way (Dispatchers):
     *                  a)Main:for light operations like update UI or call live data.
     *                  b)Default:if we didn't select what is our thread >>it will be default.>>heavy operations.
     *                  c)input/output : call any input or output like>>room input or retrofit ..etc.
     *                  d)Unconfined: very complex thread way >>make (switching) to thread what suspend fun should be with.
     *                  e)NewSingleThreadContext :custom thread
     *
     * 5)when we make UI update should be in main thread >>put main thread way or something else then withContext()
     * what is runBlocking?:make block until inside {body} finished >>working in main thread.
     * note:if I have data come from network and from local dataBase >>we should do it parallel w/o wait
     * note:if I put withContext inside suspend fun (I did not put Global scope)>>will not be in parallel cuz withContext is suspend function >>like I call suspend fun twice
     * cuz they are not connected to wait it.
     * 6)launch vs Async/Await :
     * launch:no return >>return job
     * Async/Await :return values >>there's something called (Deferred)>>will wait values and come with it
     * but in this case should use Await().,note:Deferred wait just one value
     *
     * Structured Concurrency:means if I have blocks of coroutines builders and I want to ranging it every one depends on other
     * Jobs in Coroutines? its like handle of plastic bag>> to move it .
     * 7)if parent job canceled >>every child will be canceled.or if any child has error parent will be canceled >>everyone depends on each other.
     * 8)join()&joinAll>>will do joined launch(childes)then do after join function code.
     *
     * 9) if we need to wait more than one value(list ..etc)>>use Channels,open channel btw 2 coroutines>>1 ,buffer,2 : by send&offer
     *
     * 10)if producer send (suspend) data to buffer and the collector cannot collect it >>memory leak cuz producer will be suspend for ever >>to solve this problem we use Kotlin Flows
     * Note:*HotStream>>Stream is passing data with or w/o something consume this data
     *      *ColdStream>>Stream is passing data with something consume it.
     * Note:*Channels>>producer>buffer>collector>>HotStream
     *      *Flow>>to modification channels :producer>intermediate(some processes on data before pass it to collector)>collector>>ColdStream
     * in Flow>>intermediate and producer will work inside collector coroutine>>producer and collector will not be in parallel
     * to make it in parallel>>add buffer to save data from producer without suspend producer to buffer>>producer suspend to intermediate
     *
     * LiveData>>has problems:1)fires even w/o update
     *                        2)have to use in Main thread.
     *                        3)cannot observe in  repositories
     * to solve live data problems >>use ((stateFlow))
     * 1)state: ex:loading,error,successes,or any function I want>>we make state management between
     * our functions>>ex:loading >>ok>>success>>if not ok >>error >>from error to >>load ...and so on ,this called state management
     * 2)state flow : management state with state flow instead of live data
     *
     * state flow:1)fires when new update.
     *            2)can be in background thread.
     *            3)always has value ,cannot be null(safe call).
     *            4)can use kotlin flow operators(intermediate operators).
     *            5)Not aware with life cycle like live data >> continue with observe even out of life cycle.
     */
////////////////////////////////////////////////////////////////////////////////////////
//* Coroutine Notes:
//         * 1)note:Continuation is the parameter give me power to stop and resume my process
//         * (it is hiding parameter in coroutines function "suspend fun" you can find it after make java decompiler)
//    private suspend fun printMyTextAfterDelayCoroutine(myText: String) {
    //should call in Coroutine builder or inside suspend fun
//        GlobalScope.launch(Dispatchers.IO) {
//            delay(2000)//instead of Thread.Sleep()
//            Log.d(TAG, "io thread ")
////            withContext(Dispatchers.Main) {
////                MyText.text = myText//will do fatal exception without (withContext) >>cuz we are in IO thread >>we cannot update UI thread here
////                //should use withContext()>>switching context
////            }
//        }
////////////////////////////////////////////////////////////////////////////////////////
//        *parallel and not parallel > >
//        GlobalScope.launch {
//            delay(2000)
//            Log.d(TAG, myText)
//        }
//    }//in this case Abdullah1 will print after 2 sec ,then after another 2 sec will print Abdullah2
//if we want it run with coroutine should put GlobalScope.launch here or inside first GlobalScope
////////////////////////////////////////////////////////////////////////
    //Deferred ,async/await,Job,Join
    private suspend fun getNetworkData(): String {
        delay(2000)
        return "Abdullah"
    }

    private suspend fun getDataBaseData(): String {
        delay(2000)
        return "Mohammad"
    }











