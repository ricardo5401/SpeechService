# SpeechService
Unity speech service plugin

# unity script example

using UnityEngine;
using System.Collections;
using UnityEngine.EventSystems;

public class TestPlugin : MonoBehaviour {

	bool isListening = false;

	// Use this for initialization
	void Start () {  
	}

	public void OnButtonClick()
	{
		var go = EventSystem.current.currentSelectedGameObject;
		if (go != null) {
			Debug.Log ("Clicked on : " + go.name);
		#if UNITY_ANDROID
			callService();
		#endif
		} else {
			Debug.Log ("currentSelectedGameObject is null");
		}
	}

	void callService(){
		if( !isListening ){
			using( AndroidJavaClass androidJC = new AndroidJavaClass("com.unity3d.player.UnityPlayer") ){
				using( AndroidJavaObject jo = androidJC.GetStatic<AndroidJavaObject>("currentActivity") ){
					using( AndroidJavaClass jc = new AndroidJavaClass("pe.edu.upc.speechplugin.SpeechService") ){
						jc.CallStatic("StartListenning", jo);
					}
				}
			}
			isListening = true;
		}else{ setText("Ya fue iniciado"); }
	} 

	void onActivityResult(string Translation){
		Debug.Log (Translation);
		setText(Translation);
		isListening = false;
	}

	void setText(string text){
		GameObject.Find ("TextRecognition").GetComponent<UnityEngine.UI.Text> ().text = text;
	}

	// Update is called once per frame
	void Update () {
	
	}
}

