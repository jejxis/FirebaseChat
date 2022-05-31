package net.flow9.thisiskotlin.firebasechat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import net.flow9.thisiskotlin.firebasechat.databinding.ActivityChatListBinding
import net.flow9.thisiskotlin.firebasechat.model.Room

class ChatListActivity : AppCompatActivity() {
    val binding by lazy{ ActivityChatListBinding.inflate(layoutInflater)}
    val database = Firebase.database("https://android-kotlin-firebase-debb2-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val roomsRef = database.getReference("rooms")

    val roomList = mutableListOf<Room>()
    lateinit var adapter: ChatRoomListAdapter
    companion object{//다른 액티비티에서도 조회할 수 있다
        var userId: String = ""
        var userName: String = ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        userId = intent.getStringExtra("userId")?: "none"
        userName = intent.getStringExtra("userName")?: "Anonymous"

        adapter = ChatRoomListAdapter(roomList)
        with(binding){
            btnCreate.setOnClickListener { openCreateRoom() }

            recyclerRooms.adapter = adapter
            recyclerRooms.layoutManager = LinearLayoutManager(baseContext)
        }

        loadRooms()
    }

    fun loadRooms(){
        roomsRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //방 목록 삭제
                roomList.clear()
                for(item in snapshot.children){
                    item.getValue(Room::class.java)?.let{ room ->
                        //방 목록 추가
                        roomList.add(room)
                    }
                }
                adapter.notifyDataSetChanged()//어댑터 갱신
            }

            override fun onCancelled(error: DatabaseError) {
                print(error.message)
            }
        })
    }

    fun openCreateRoom(){
        //방 이름 입력할 EditText 생성
        val editTitle = EditText(this)
        //다이얼로그 생성
        val dialog = AlertDialog.Builder(this)
            .setTitle("방 이름")
            .setView(editTitle)
            .setPositiveButton("만들기"){dlg, id ->
                //방 이름 입력 여부 체크
                createRoom(editTitle.text.toString())
            }
        dialog.show()
    }

    fun createRoom(title: String){
        val room = Room(title, userName)//방 데이터 생성
        val roomId = roomsRef.push().key!! //방 아이디 만들어서 입력
        room.id = roomId
        roomsRef.child(roomId).setValue(room)//파이어베이스에 전송
    }
}
class ChatRoomListAdapter(val roomList:MutableList<Room>): RecyclerView.Adapter<ChatRoomListAdapter.Holder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val room = roomList.get(position)
        holder.setRoom(room)
    }

    override fun getItemCount(): Int {
        return roomList.size
    }

    class Holder(itemView: View): RecyclerView.ViewHolder(itemView){
        lateinit var mRoom:Room
        init{
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ChatRoomActivity::class.java)
                intent.putExtra("roomId", mRoom.id)
                intent.putExtra("roomTitle", mRoom.title)
                itemView.context.startActivity(intent)
            }
        }
        fun setRoom(room:Room){
            this.mRoom = room
            itemView.findViewById<TextView>(android.R.id.text1).setText(room.title)
        }
    }
}