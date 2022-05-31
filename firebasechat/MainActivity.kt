package net.flow9.thisiskotlin.firebasechat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import net.flow9.thisiskotlin.firebasechat.databinding.ActivityMainBinding
import net.flow9.thisiskotlin.firebasechat.model.User

class MainActivity : AppCompatActivity() {
    val binding by lazy{ ActivityMainBinding.inflate(layoutInflater)}
    val database = Firebase.database("https://android-kotlin-firebase-debb2-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val usersRef = database.getReference("users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding){
            btnSignin.setOnClickListener { signin() }
            btnSignup.setOnClickListener { signup() }
        }
    }
    fun signup(){
        with(binding){
            //입력된 값 가져오기
            val id = editId.text.toString()
            val password = editPassword.text.toString()
            val name = editName.text.toString()

            //모든 값이 있는지 검사
            if(id.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()){
                usersRef.child(id).get().addOnSuccessListener {
                    //데이터 베이스에 아이디가 존재하는지 검사
                    if(it.exists()){
                        Toast.makeText(baseContext, "아이디가 존재합니다.", Toast.LENGTH_LONG).show()
                    }else{
                        //없으면 저장 후 자동 로그인
                        val user = User(id, password, name)
                        usersRef.child(id).setValue(user)
                        signin()
                    }
                }
            }else{//입력 필드가 비어있을 경우
                Toast.makeText(baseContext, "아이디, 비밀번호, 별명을 모두 입력해야 합니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun signin(){
        with(binding){
            //입력된 값 가져오기
            val id = editId.text.toString()
            val password = editPassword.text.toString()

            if(id.isNotEmpty() && password.isNotEmpty()){
                //아이디로 유저 데이터 가져오기
                usersRef.child(id).get().addOnSuccessListener {
                    //id 존재 확인
                    if(it.exists()){
                        it.getValue(User::class.java)?.let{user ->
                            //비밀번호 비교하여 같으면 채팅방 목록으로 이동
                            if(user.password == password){
                                goChatroomList(user.id, user.name)
                            }else{
                                Toast.makeText(baseContext, "비밀번호가 다릅니다.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }else{
                        Toast.makeText(baseContext, "아이디가 없습니다.", Toast.LENGTH_LONG).show()
                    }
                }
            }else{
                Toast.makeText(baseContext, "아이디, 비밀번호를 입력해야 합니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun goChatroomList(userId: String, userName: String){
        val intent = Intent(this, ChatListActivity::class.java)

        //방 생성 또는 입장
        intent.putExtra("userId", userId)
        intent.putExtra("userName", userName)
        startActivity(intent)
    }
}